package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.AlgorithmSettings;
import com.example.model.MyGraph;

public class SimpleSimulation implements Simulation {

    private MyGraph graph;

    public SimpleSimulation(MyGraph graph) {
        this.graph = graph;
    }

    @Override
    public void start(Algorithm algorithm, AlgorithmSettings settings) {
        algorithm.execute(graph, (int)settings.getSettings().get("depth"));
    }
}
