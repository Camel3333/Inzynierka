package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import lombok.Getter;
import lombok.Setter;

public class CreationHelper {
    @Setter
    private Graph<Integer, Integer> graph;
    @Getter
    private DrawMode currentDrawMode = DrawMode.EDGE;
    private Vertex<Integer> selectedToEdge;

    public void selectVertex(Vertex<Integer> vertex){
        System.out.println("SELECT VERTEX WAS CALLED");
        if (!checkCreationCondition(DrawMode.EDGE)) {
            return;
        }
        if (selectedToEdge == null) {
            selectedToEdge = vertex;
        }
        else {
            graph.insertEdge(selectedToEdge, vertex, 1);
            selectedToEdge = null;
        }
    }

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
}
