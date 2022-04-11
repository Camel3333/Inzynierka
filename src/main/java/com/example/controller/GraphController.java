package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    private SmartGraphDemoContainer container;
    private SmartGraphPanel graphView;

    private void buildGraph() {
        Graph graph = new GraphEdgeList();
        graph.insertVertex("A");
        graph.insertVertex("B");
        graph.insertVertex("C");
        graph.insertVertex("D");
        graph.insertVertex("E");
        graph.insertVertex("F");
        graph.insertVertex("G");

        graph.insertEdge("A", "B", "1");
        graph.insertEdge("A", "C", "2");
        graph.insertEdge("A", "D", "3");
        graph.insertEdge("A", "E", "4");
        graph.insertEdge("A", "F", "5");
        graph.insertEdge("A", "G", "6");

        graph.insertVertex("H");
        graph.insertVertex("I");
        graph.insertVertex("J");
        graph.insertVertex("K");
        graph.insertVertex("L");
        graph.insertVertex("M");
        graph.insertVertex("N");

        graph.insertEdge("H", "I", "7");
        graph.insertEdge("H", "J", "8");
        graph.insertEdge("H", "K", "9");
        graph.insertEdge("H", "L", "10");
        graph.insertEdge("H", "M", "11");
        graph.insertEdge("H", "N", "12");

        graph.insertEdge("A", "H", "0");

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);
        container = new SmartGraphDemoContainer(graphView);
    }

    public void initGraph() {
        graphView.init();
    }

    @FXML
    public void initialize() {
        buildGraph();
        container.prefWidthProperty().bind(graphRoot.widthProperty());
        container.prefHeightProperty().bind(graphRoot.heightProperty());
        graphRoot.getChildren().add(container);
    }
}
