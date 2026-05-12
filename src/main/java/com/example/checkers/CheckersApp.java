package com.example.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.example.checkers.PieceType.*;

public class CheckersApp extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private int turn = 0;
    private final Tile[][] board = new Tile[WIDTH][HEIGHT];
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();
    private Piece mustStrike = null;

    private Controller controller;
    private final StopWatch stopwatch = new StopWatch();

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createContent());
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.show();
        stopwatch.start();
    }

    private Parent createContent() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Board.fxml")));
        Pane root = loader.load();
        controller = loader.getController();
        controller.bindTimer(stopwatch.textProperty());

        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) piece = makePiece(RED, x, y);
                if (y >= 5 && (x + y) % 2 != 0) piece = makePiece(WHITE, x, y);

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        return root;
    }

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
        // even turns: white's turn; odd turns: red's turn
        return isRed == (turn % 2 == 0);
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
        if (canCaptureInDirection(type, x, y, 1, fwd)) return true;
        if (type.isKing()) {
            int bwd = type.secondaryDir;
            if (canCaptureInDirection(type, x, y, -1, bwd)) return true;
            if (canCaptureInDirection(type, x, y, 1, bwd)) return true;
        }
        return false;
    }

    private Piece checkIfAnyPieceCanCapture(PieceType type) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[x][y].hasPiece() && board[x][y].getPiece().getType() == type
                        && canCaptureAgain(type, x, y)) {
                    return board[x][y].getPiece();
                }
            }
        }
        return null;
    }

    private Piece makePiece(PieceType type, int x, int y) throws IOException {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e -> {
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
                    if (capturedIsRed) {
                        controller.decrementRed();
                    } else {
                        controller.decrementWhite();
                    }

                    if (controller.isGameOver()) {
                        stopwatch.stop();
                    }
                }
            }
        });
        return piece;
    }

    private void promoteToKingIfNeeded(Piece piece, int y) {
        try {
            if (piece.getType() == RED && y == HEIGHT - 1) {
                piece.promoteToKing(REDKING, getClass().getResource("RedKingPiece.fxml"));
            } else if (piece.getType() == WHITE && y == 0) {
                piece.promoteToKing(WHITEKING, getClass().getResource("WhiteKingPiece.fxml"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    public static void main(String[] args) {
        launch();
    }
}
