package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    private int counter = 0;
    private SmartGraphDemoContainer container;
    private SmartGraphPanel<Integer, Integer> graphView;
    @Getter
    private Graph<Integer, Integer> graph = new MyGraph<>();

    private void buildGraph() {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.setVertexDoubleClickAction(graphVertex -> {
            MyVertex<Integer> vertex = (MyVertex<Integer>)graphVertex.getUnderlyingVertex();
            vertex.setTraitor(!vertex.isTraitor());
            if (vertex.isTraitor()) {
                graphView.getStylableVertex(vertex).setStyleClass("traitor");
            } else {
                graphView.getStylableVertex(vertex).setStyleClass("vertex");
            }
        });
        container = new SmartGraphDemoContainer(graphView);
    }

    public void initGraph() {
        graphView.init();
    }

    // TODO: implement update as listener to graph changes
    public void update(){
        graphView.update();
    }

    // only for test purposes
    public void addExampleVertex(){
        graph.insertVertex(counter++);
        update();
    }

    // only for test purposes
    public void removeLastVertex(){
        var vertex = graph.vertices().stream().filter(v -> v.element().equals(counter-1)).findFirst();
        graph.removeVertex(vertex.get());
        counter--;
        update();
    }

    @FXML
    public void initialize() {
        buildGraph();
        container.prefWidthProperty().bind(graphRoot.widthProperty());
        container.prefHeightProperty().bind(graphRoot.heightProperty());
        graphRoot.getChildren().add(container);
    }
}
