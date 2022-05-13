package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

import java.net.URI;

public class MySmartGraphPanel<V,E> extends SmartGraphPanel<V,E> {
    @Getter
    private BooleanProperty ableToInit = new SimpleBooleanProperty(false);

    public MySmartGraphPanel(Graph<V, E> theGraph) {
        super(theGraph);
        bindInitialized();
    }

    public MySmartGraphPanel(Graph<V, E> theGraph, SmartGraphProperties properties) {
        super(theGraph, properties);
        bindInitialized();
    }

    public MySmartGraphPanel(Graph<V, E> theGraph, SmartPlacementStrategy placementStrategy) {
        super(theGraph, placementStrategy);
        bindInitialized();
    }

    public MySmartGraphPanel(Graph<V, E> theGraph, SmartGraphProperties properties, SmartPlacementStrategy placementStrategy) {
        super(theGraph, properties, placementStrategy);
        bindInitialized();
    }

    public MySmartGraphPanel(Graph<V, E> theGraph, SmartGraphProperties properties, SmartPlacementStrategy placementStrategy, URI cssFile) {
        super(theGraph, properties, placementStrategy, cssFile);
        bindInitialized();
    }

    private void bindInitialized(){
        ableToInit.bind(Bindings.and(widthProperty().greaterThan(0.0), heightProperty().greaterThan(0.0)));
    }
}
