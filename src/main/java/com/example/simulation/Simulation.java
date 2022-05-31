package com.example.simulation;

public interface Simulation {
    void render();
    void reset();
    void resetToRound(int round);
    void goToRound(int round);
    void finish();
}
