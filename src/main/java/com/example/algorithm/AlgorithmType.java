package com.example.algorithm;

import com.example.util.NotImplementedException;

public enum AlgorithmType {
    LAMPORT;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public Algorithm getAlgorithm() {
        switch (this) {
            case LAMPORT -> {
                return new LamportAlgorithm();
            }
            default -> throw new NotImplementedException(this.toString() + " algorithm not implemented");
        }
    }
}
