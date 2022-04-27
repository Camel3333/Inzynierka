package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Vertex> vertices = new ArrayList<>();

    public void addVertex(Vertex v){
        vertices.add(v);
    }

    public void removeVertex(Vertex v){
        vertices.remove(v);
    }
}
