package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.VertexRole;
import com.example.controller.GraphController;

import java.util.Map;

public class LamportAnimationEngine extends AnimationEngine{
    public LamportAnimationEngine(GraphController graphController) {
        super();
        this.graphController = graphController;
    }

    @Override
    protected void highlightRoles(Map<Vertex<Integer>, VertexRole> roles) {
        for (Map.Entry<Vertex<Integer>, VertexRole> entry : roles.entrySet()){
            switch (entry.getValue()){
                case COMMANDER -> System.out.println("Vertex "+entry.getKey().element()+" is commander");
                case LIEUTENANT -> System.out.println("Vertex "+entry.getKey().element()+" is lieutenant");
            }
        }
    }
}
