package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import org.springframework.stereotype.Component;

@Component
public class GraphGenerator {

    public void generateGraph(GraphController graphController, DefinedGraph definedGraph){
        Graph<Integer, Integer> generatedGraph;
        switch (definedGraph) {
            case FULL_3 -> generatedGraph = getFullGraph(3);
            case FULL_4 -> generatedGraph = getFullGraph(4);
            case FULL_5 -> generatedGraph = getFullGraph(5);
            case FULL_6 -> generatedGraph = getFullGraph(6);
            default -> throw new IllegalStateException("Unexpected value: " + definedGraph);
        }
        graphController.setModelGraph(generatedGraph);
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
