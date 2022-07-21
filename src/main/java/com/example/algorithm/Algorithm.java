package com.example.algorithm;

import com.example.algorithm.operations.Operation;
import com.example.algorithm.report.StepReport;
import com.example.model.MyGraph;
import com.example.settings.AlgorithmSettings;

import java.util.List;

public interface Algorithm {
    AlgorithmType getType();
    void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings);
    StepReport step();
    boolean isFinished();
}
