package com.example.algorithm;

import com.example.util.NotImplementedException;

public enum AlgorithmType {
    LAMPORT,
    KING,
    QVOTER;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public Algorithm getAlgorithm() {
        switch (this) {
            case LAMPORT -> {return new LamportIterAlgorithm();}
            case KING -> {return new KingAlgorithm();}
            case QVOTER -> {return new QVoterModel();}
            default -> throw new NotImplementedException(this.toString() + " algorithm not implemented");
        }
    }
}
