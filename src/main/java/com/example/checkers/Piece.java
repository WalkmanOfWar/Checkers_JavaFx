package com.example.checkers;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static com.example.checkers.CheckersApp.TILE_SIZE;

public class Piece extends StackPane {
    private PieceType type;
    private double mouseX, mouseY;
    private double oldX, oldY;

    public Piece(PieceType type, int x, int y) throws IOException {
        this.type = type;
        move(x, y);

        String fxmlName = switch (type) {
            case RED -> "RedPiece.fxml";
            case WHITE -> "WhitePiece.fxml";
            case REDKING -> "RedKingPiece.fxml";
            case WHITEKING -> "WhiteKingPiece.fxml";
        };

        Ellipse visual = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlName)));
        getChildren().add(visual);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });
        setOnMouseDragged(e -> relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY));
    }

    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }

    public void promoteToKing(PieceType kingType, URL fxmlUrl) throws IOException {
        this.type = kingType;
        Ellipse kingVisual = FXMLLoader.load(Objects.requireNonNull(fxmlUrl));
        getChildren().set(0, kingVisual);
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }
}
