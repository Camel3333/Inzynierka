package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import org.springframework.stereotype.Component;

@Component
public class GraphGenerator {

    public void generateGraph(GraphController graphController, DefinedGraph definedGraph, int numberOfVertices){
        Graph<Integer, Integer> generatedGraph;
        switch (definedGraph) {
            case FULL -> generatedGraph = getFullGraph(numberOfVertices);
            default -> throw new IllegalStateException("Unexpected value: " + definedGraph);
        }
        graphController.setModelGraph((MyGraph<Integer, Integer>) generatedGraph);
    }

    private Graph<Integer, Integer> getFullGraph(int numberOfVertices) {
        Graph<Integer, Integer> graph = new MyGraph<>();
        for (int i = 0; i < numberOfVertices; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < numberOfVertices - 1; i++) {
            for (int j = i + 1; j < numberOfVertices; j++){
                graph.insertEdge(i, j, 1);
            }
        }
        return graph;
    }
}
