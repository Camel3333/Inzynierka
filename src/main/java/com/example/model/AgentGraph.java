package com.example.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EqualsAndHashCode
public class AgentGraph {
    @Getter
    private ObservableList<AgentVertex> vertices = FXCollections.observableArrayList();

    public void addVertex(AgentVertex v){
        vertices.add(v);
    }

    public void removeVertex(AgentVertex v){
        vertices.remove(v);
        List<AgentVertex> neighboursToRemove = List.copyOf(v.getNeighbours());
        neighboursToRemove.forEach(v::removeNeighbour);
    }

}
