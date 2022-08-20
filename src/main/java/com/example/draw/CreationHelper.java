package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.controller.GraphController;
import com.example.util.GraphObserver;
import lombok.Getter;
import lombok.Setter;

public class CreationHelper implements GraphObserver<Integer, Integer> {
    @Setter
    @Getter
    private GraphController graphController;
    @Getter
    private DrawMode currentDrawMode = DrawMode.NONE;
    private Vertex<Integer> selectedToEdge;

    public void setDrawMode(DrawMode mode){
        if (currentDrawMode != mode){
            cleanCache();
            currentDrawMode = mode;
        }
    }

    private void cleanCache(){
        selectedToEdge = null;
    }

    private boolean checkCreationCondition(DrawMode mode){
        return currentDrawMode == mode;
    }

    @Override
    public void vertexClicked(Vertex<Integer> vertex) {
        System.out.println("SELECT VERTEX WAS CALLED");
        switch (currentDrawMode){
            case EDGE -> {
                if (selectedToEdge == null) {
                    selectedToEdge = vertex;
                }
                else {
                    graphController.getGraph().insertEdge(selectedToEdge, vertex, 1);
                    selectedToEdge = null;
                    graphController.update();
                }
            }
            case DELETE -> {
                graphController.getGraph().removeVertex(vertex);
                graphController.update();
            }
        }
    }

    @Override
    public void edgeClicked(Edge<Integer, Integer> edge) {
        System.out.println("SELECT EDGE WAS CALLED");
        switch (currentDrawMode){
            case DELETE -> {
                graphController.getGraph().removeEdge(edge);
                graphController.update();
            }
        }
    }

    @Override
    public void vertexDoubleClicked(Vertex<Integer> vertex) {

    }

    @Override
    public void edgeDoubleClicked(Edge<Integer, Integer> edge) {

    }

    @Override
    public void clickedAt(double x, double y) {
        switch (currentDrawMode){
            case VERTEX -> {
                // add vertex at clicked position
                var vertex = graphController.getGraph().insertVertex(graphController.getNextVertexId());
                graphController.update();
                graphController.setVertexPosition(vertex, x, y);
                graphController.colorVertex(vertex);
            }
        }
    }
}
