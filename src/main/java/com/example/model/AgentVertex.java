package com.example.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class AgentVertex {
    private final int id;
    @Getter
    private ObservableList<AgentVertex> neighbours = FXCollections.observableArrayList();
    private Opinions opinions;
    private Map<AgentVertex, Opinions> knowledge = new HashMap<>();

    public AgentVertex(int id) {
        this.id = id;
    }

    public void addNeighbour(AgentVertex v){
        neighbours.add(v);
    }

    public void removeNeighbour(AgentVertex v){
        neighbours.remove(v);
    }
}
