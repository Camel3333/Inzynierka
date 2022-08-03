package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.VertexRole;
import com.example.controller.GraphController;

import java.util.Map;

public class QVoterAnimationEngine extends AnimationEngine{
    public QVoterAnimationEngine(GraphController graphController) {
        super(graphController);
    }

    @Override
    protected void highlightRoles(Map<Vertex<Integer>, VertexRole> roles) {
        //todo
    }
}
