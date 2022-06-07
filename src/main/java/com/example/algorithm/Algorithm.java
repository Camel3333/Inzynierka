package com.example.algorithm;

import com.example.model.MyGraph;

public interface Algorithm {
    AlgorithmType getType();
    void execute(MyGraph<Integer, Integer> graph, int depth);
}
