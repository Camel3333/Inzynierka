package com.example.algorithm;

import com.example.model.MyGraph;
import com.example.settings.AlgorithmSettings;

public interface Algorithm {
    void execute(MyGraph<Integer, Integer> graph, AlgorithmSettings settings);
}
