package com.example.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class AgentVertex {
    @Getter
    private final int id;
    @Getter
    private ObservableList<AgentVertex> neighbours = FXCollections.observableArrayList();
    private Opinions opinions;
    private Map<AgentVertex, Opinions> knowledge = new HashMap<>();

    public AgentVertex(int id) {
        this.id = id;
    }

    public void addNeighbour(AgentVertex v){
        if (!neighbours.contains(v)) {
            neighbours.add(v);
            v.addNeighbour(this);
        }
    }

    public void removeNeighbour(AgentVertex v){
        if (neighbours.contains(v)) {
            neighbours.remove(v);
            v.removeNeighbour(this);
        }
    }
}
