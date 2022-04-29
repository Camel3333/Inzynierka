package com.example.model;

import com.brunomnsilva.smartgraph.graph.Vertex;

import java.util.HashMap;
import java.util.Map;

public class MyVertex<V> implements Vertex<V> {
    private V id;
    private boolean isTraitor;
    private Opinions opinions;
    private Map<MyVertex<V>, Opinions> knowledge = new HashMap<>();

    public MyVertex(V id){
        this.id = id;
    }

    public void setElement(V element){
        id = element;
    }

    @Override
    public V element() {
        return id;
    }
}
