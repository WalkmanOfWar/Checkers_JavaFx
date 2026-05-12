package com.example.checkers;

public enum PieceType {
    RED(1, 0),
    WHITE(-1, 0),
    REDKING(1, -1),
    WHITEKING(-1, 1);

    final int moveDir;
    final int secondaryDir; // reverse diagonal direction for kings; 0 for regular pieces

    PieceType(int moveDir, int secondaryDir) {
        this.moveDir = moveDir;
        this.secondaryDir = secondaryDir;
    }

    public boolean isKing() {
        return secondaryDir != 0;
    }
}
