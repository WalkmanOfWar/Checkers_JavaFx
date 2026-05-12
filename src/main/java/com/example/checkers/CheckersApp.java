package com.example.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.example.checkers.PieceType.*;

public class CheckersApp extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH  = 8;
    public static final int HEIGHT = 8;

    /** Pixel offset so the board has breathing room from the window edge.
     *  Must be >= piece radiusX (36 px) so column-0 pieces are never clipped. */
    private static final int BOARD_OFFSET = 40;

    private int turn = 0;
    private final Tile[][] board = new Tile[WIDTH][HEIGHT];
    private final Group tileGroup  = new Group();
    private final Group pieceGroup = new Group();
    private Piece mustStrike = null;

    private Controller controller;
    private final StopWatch stopwatch = new StopWatch();

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createContent());
        stage.setTitle("Warcaby");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stopwatch.start();
    }

    private Parent createContent() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Board.fxml")));
        Pane root = loader.load();
        controller = loader.getController();
        controller.bindTimer(stopwatch.textProperty());

        // Offset the board groups so tiles are inset from the window edge
        tileGroup.setTranslateX(BOARD_OFFSET);
        tileGroup.setTranslateY(BOARD_OFFSET);
        pieceGroup.setTranslateX(BOARD_OFFSET);
        pieceGroup.setTranslateY(BOARD_OFFSET);

        // Soft drop shadow on the board surface
        tileGroup.setEffect(new DropShadow(18, 3, 3, Color.web("#00000088")));

        // Thin gold border drawn on top of tiles, below pieces
        Rectangle boardBorder = new Rectangle(
                BOARD_OFFSET, BOARD_OFFSET,
                WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        boardBorder.setFill(Color.TRANSPARENT);
        boardBorder.setStroke(Color.web("#8B6914"));
        boardBorder.setStrokeWidth(2);
        boardBorder.setMouseTransparent(true);

        root.getChildren().add(tileGroup);
        root.getChildren().add(boardBorder);
        root.getChildren().add(pieceGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) piece = makePiece(RED,   x, y);
                if (y >= 5 && (x + y) % 2 != 0) piece = makePiece(WHITE, x, y);

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        return root;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Move validation
    // ─────────────────────────────────────────────────────────────────────────

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0 || isWrongTurn(piece.getType())) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        PieceType type = piece.getType();
        int dx = newX - x0;
        int dy = newY - y0;

        mustStrike = checkIfAnyPieceCanCapture(type);

        boolean isNormalMove = Math.abs(dx) == 1
                && (dy == type.moveDir || dy == type.secondaryDir)
                && mustStrike == null;

        boolean isCaptureMove = Math.abs(dx) == 2
                && (dy == type.moveDir * 2 || dy == type.secondaryDir * 2);

        if (isNormalMove) {
            turn++;
            stopwatch.reset();
            return new MoveResult(MoveType.NORMAL);
        }

        if (isCaptureMove) {
            int midX = x0 + dx / 2;
            int midY = y0 + dy / 2;
            if (board[midX][midY].hasPiece() && isOpponent(type, board[midX][midY].getPiece().getType())) {
                if (canCaptureAgain(type, newX, newY)) {
                    mustStrike = piece;
                } else {
                    turn++;
                    stopwatch.reset();
                    mustStrike = null;
                }
                return new MoveResult(MoveType.CAPTURE, board[midX][midY].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private boolean isWrongTurn(PieceType type) {
        boolean isRed = type == RED || type == REDKING;
        return isRed == (turn % 2 == 0); // even turns: white; odd turns: red
    }

    private boolean isOpponent(PieceType mover, PieceType other) {
        boolean moverIsRed = mover == RED || mover == REDKING;
        boolean otherIsRed = other == RED || other == REDKING;
        return moverIsRed != otherIsRed;
    }

    private boolean canCaptureInDirection(PieceType type, int x, int y, int dx, int dy) {
        int midX = x + dx, midY = y + dy;
        int landX = x + 2 * dx, landY = y + 2 * dy;
        if (landX < 0 || landX >= WIDTH || landY < 0 || landY >= HEIGHT) return false;
        return board[midX][midY].hasPiece()
                && isOpponent(type, board[midX][midY].getPiece().getType())
                && !board[landX][landY].hasPiece();
    }

    boolean canCaptureAgain(PieceType type, int x, int y) {
        int fwd = type.moveDir;
        if (canCaptureInDirection(type, x, y, -1, fwd)) return true;
        if (canCaptureInDirection(type, x, y,  1, fwd)) return true;
        if (type.isKing()) {
            int bwd = type.secondaryDir;
            if (canCaptureInDirection(type, x, y, -1, bwd)) return true;
            if (canCaptureInDirection(type, x, y,  1, bwd)) return true;
        }
        return false;
    }

    private Piece checkIfAnyPieceCanCapture(PieceType type) {
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                if (board[x][y].hasPiece() && board[x][y].getPiece().getType() == type
                        && canCaptureAgain(type, x, y))
                    return board[x][y].getPiece();
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Piece creation & move execution
    // ─────────────────────────────────────────────────────────────────────────

    private Piece makePiece(PieceType type, int x, int y) throws IOException {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e -> {
            piece.resetScale();

            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());
            MoveResult result = tryMove(piece, newX, newY);
            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE -> piece.abortMove();

                case NORMAL -> {
                    promoteToKingIfNeeded(piece, newY);
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                }

                case CAPTURE -> {
                    promoteToKingIfNeeded(piece, newY);
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece captured = result.getCapturedPiece();
                    board[toBoard(captured.getOldX())][toBoard(captured.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(captured);

                    boolean capturedIsRed = captured.getType() == RED || captured.getType() == REDKING;
                    if (capturedIsRed) controller.decrementRed();
                    else               controller.decrementWhite();

                    if (controller.isGameOver()) stopwatch.stop();
                }
            }
        });
        return piece;
    }

    private void promoteToKingIfNeeded(Piece piece, int y) {
        try {
            if (piece.getType() == RED   && y == HEIGHT - 1)
                piece.promoteToKing(REDKING,   getClass().getResource("RedKingPiece.fxml"));
            else if (piece.getType() == WHITE && y == 0)
                piece.promoteToKing(WHITEKING, getClass().getResource("WhiteKingPiece.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    public static void main(String[] args) {
        launch();
    }
}
