package com.example.draw;

public enum DefinedGraph {
    FULL;

    @Override
    public String toString() {
        return switch (this) {
            case FULL -> "Full graph";
        };
    }
}
