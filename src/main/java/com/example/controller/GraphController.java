package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.model.AgentGraph;
import com.example.model.AgentVertex;
import com.example.model.MyGraph;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    private int counter = 0;
    private SmartGraphDemoContainer container;
    private SmartGraphPanel graphView;
    private Graph<Integer, Integer> graph = new MyGraph<>();

    public GraphController() {
//        Vertex<Integer> v1 = graph.insertVertex(1);
//        Vertex<Integer> v2 = graph.insertVertex(2);
//
//        graph.insertEdge(v1, v2, 1);
    }

    private void buildGraph() {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);
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
    }

    // only for test purposes
    public void removeLastVertex(){
        var vertex = graph.vertices().stream().filter(v -> v.element().equals(counter-1)).findFirst();
        graph.removeVertex(vertex.get());
        counter--;
    }

    @FXML
    public void initialize() {
        buildGraph();
        container.prefWidthProperty().bind(graphRoot.widthProperty());
        container.prefHeightProperty().bind(graphRoot.heightProperty());
        graphRoot.getChildren().add(container);
    }
}
