package com.example.algorithm;

public enum AlgorithmType {
    LAMPORT;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
