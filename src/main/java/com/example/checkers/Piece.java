package com.example.checkers;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

import java.io.IOException;
import java.util.Objects;

import static com.example.checkers.CheckersApp.TILE_SIZE;

public class Piece extends StackPane {
    private PieceType type;
    private double mouseX, mouseY;
    private double oldX, oldY;

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public Piece(PieceType type, int x, int y) throws IOException {
        this.type = type;
        move(x, y);
        Ellipse bg;
        switch (type) {
            case RED -> bg = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RedPiece.fxml")));
            case WHITE -> bg = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("WhitePiece.fxml")));
            default -> bg = null;
        }

        getChildren().add(bg);
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
}
