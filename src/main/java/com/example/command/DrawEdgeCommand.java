package com.example.command;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.controller.GraphController;

public class DrawEdgeCommand implements Command {

    private GraphController graphController;
    private Integer firstVertexIndex;
    private Integer secondVertexIndex;

    public DrawEdgeCommand(GraphController graphController, Vertex<Integer> firstVertex, Vertex<Integer> secondVertex) {
        this.graphController = graphController;
        this.firstVertexIndex = firstVertex.element();
        this.secondVertexIndex = secondVertex.element();
    }

    @Override
    public void execute() {
        graphController.getGraph().insertEdge(firstVertexIndex, secondVertexIndex, 1);
        graphController.update();
    }

    @Override
    public void undo() {
        var edges = graphController.getGraph().edges();
        Edge<Integer, Integer> foundEdge = null;
        for(Edge<Integer, Integer> edge : edges) {
            var vertices = edge.vertices();
            if ((vertices[0].element().equals(firstVertexIndex) && vertices[1].element().equals(secondVertexIndex))
                    || (vertices[0].element().equals(secondVertexIndex) && vertices[1].element().equals(firstVertexIndex))) {
                foundEdge = edge;
                break;
            }
        }
        graphController.getGraph().removeEdge(foundEdge);
        graphController.update();
    }

    @Override
    public void redo() {
        execute();
    }

    @Override
    public String getName() {
        return null;
    }
}
