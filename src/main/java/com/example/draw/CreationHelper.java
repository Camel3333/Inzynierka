package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;

public class CreationHelper {
    private Graph<Integer, Integer> graph;
    private DrawMode currentDrawMode;
    private Vertex<Integer> selectedToEdge;

    public void selectVertex(Vertex<Integer> vertex){
        if (!checkCreationCondition(DrawMode.EDGE))
            return;
        if (selectedToEdge != null){
            selectedToEdge = vertex;
        }
        else{
            graph.insertEdge(selectedToEdge, vertex, 1);
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
