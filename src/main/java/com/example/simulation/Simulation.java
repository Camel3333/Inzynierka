package com.example.simulation;

//public interface Simulation {
//    void render();
//    void reset();
//    void resetToRound(int round);
//    void goToRound(int round);
//    void finish();
//}

import com.example.algorithm.Algorithm;
import com.example.algorithm.AlgorithmSettings;

public interface Simulation {
    void start(Algorithm algorithm, AlgorithmSettings settings);
}

