package com.example.checkers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.example.checkers.CheckersApp.TILE_SIZE;

public class Tile extends Rectangle {
    private Piece piece;

    public Tile(boolean light, int x, int y) {
        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(x * TILE_SIZE, y * TILE_SIZE);
        // Classic warm wood palette
        setFill(light ? Color.valueOf("#F0D9B5") : Color.valueOf("#8B4513"));
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
