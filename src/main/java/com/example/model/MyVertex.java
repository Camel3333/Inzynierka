package com.example.model;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class MyVertex<V> implements Vertex<V>, Agent {
    private V id;
    @Getter
    private BooleanProperty isTraitor = new SimpleBooleanProperty();
    @Getter
    @Setter
    private BooleanProperty supportsOpinion = new SimpleBooleanProperty();
    @Getter
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

    @Override
    public BooleanProperty isTraitor() {
        return isTraitor;
    }

    @Override
    public void setIsTraitor(boolean isTraitor) {
        this.isTraitor.setValue(isTraitor);
    }
}
