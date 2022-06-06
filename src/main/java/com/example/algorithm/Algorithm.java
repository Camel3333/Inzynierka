package com.example.algorithm;

import com.example.model.MyGraph;

public interface Algorithm {
    void execute(MyGraph<Integer, Integer> graph, int depth);
}
