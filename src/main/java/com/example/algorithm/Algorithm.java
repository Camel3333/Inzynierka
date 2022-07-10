package com.example.algorithm;

import com.example.model.MyGraph;
import com.example.settings.AlgorithmSettings;

import java.util.List;

public interface Algorithm {
    void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings);
    List<Operation> step();
    boolean isFinished();
}
