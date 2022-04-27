package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vertex {
    private List<Vertex> neighbours = new ArrayList<>();
    private Opinions opinions;
    private Map<Vertex, Opinions> knowledge = new HashMap<>();

    public void addNeighbour(Vertex v){
        neighbours.add(v);
    }

    public void removeNeighbour(Vertex v){
        neighbours.remove(v);
    }
}
