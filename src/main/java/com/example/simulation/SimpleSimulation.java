package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.AlgorithmSettings;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import lombok.Setter;

public class SimpleSimulation implements Simulation {

    @Setter
    private GraphController graphController;

    public SimpleSimulation(GraphController graphController) {
        this.graphController = graphController;
    }

    @Override
    public void start(Algorithm algorithm, AlgorithmSettings settings) {
        algorithm.execute((MyGraph<Integer, Integer>) graphController.getGraph(), settings);
        graphController.update();
    }
}
