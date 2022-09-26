package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.report.StepReport;
import com.example.settings.AlgorithmSettings;

public interface Simulation {
    void setEnvironment(Algorithm algorithm, AlgorithmSettings settings);

    void allowAnimations(boolean allow);

    StepReport step();

    void stop();

    void setAnimationsSpeed(double speedMultiplier);
}

