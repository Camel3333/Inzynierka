package com.example.draw;

public enum DefinedGraph {
    FULL,
    CYCLE,
    TREE,
    BIPARTITE,
    PLANAR;

    @Override
    public String toString() {
        return switch (this) {
            case FULL -> "Full graph";
            case CYCLE -> "Cycle graph";
            case TREE -> "Tre graph";
            case BIPARTITE -> "Bipartite graph";
            case PLANAR -> "Planar graph";
        };
    }
}
