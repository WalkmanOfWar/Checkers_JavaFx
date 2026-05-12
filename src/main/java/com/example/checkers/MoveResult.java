package com.example.checkers;

public class MoveResult {
    private final MoveType type;
    private final Piece capturedPiece;

    public MoveResult(MoveType type) {
        this(type, null);
    }

    public MoveResult(MoveType type, Piece capturedPiece) {
        this.type = type;
        this.capturedPiece = capturedPiece;
    }

    public MoveType getType() {
        return type;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}
