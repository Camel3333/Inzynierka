package com.example.draw;

public enum DefinedGraph {
    FULL_3,
    FULL_4,
    FULL_5,
    FULL_6;

    @Override
    public String toString() {
        return switch (this) {
            case FULL_3 -> "Full graph with 3 vertices";
            case FULL_4 -> "Full graph with 4 vertices";
            case FULL_5 -> "Full graph with 5 vertices";
            case FULL_6 -> "Full graph with 6 vertices";
        };
    }
}
