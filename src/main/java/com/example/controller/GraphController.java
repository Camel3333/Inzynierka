package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.PopOver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    @Autowired
    private FxWeaver fxWeaver;

    private int counter = 0;
    private SmartGraphDemoContainer container;
    private SmartGraphPanel<Integer, Integer> graphView;
    @Getter
    private Graph<Integer, Integer> graph = new MyGraph<>();

    private void buildGraph() {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);
        setGraphViewBindings();

        container = new SmartGraphDemoContainer(graphView);
    }

    private void setGraphViewBindings(){
        graphView.setVertexDoubleClickAction(graphVertex -> {
            // load popUp view
            FxControllerAndView<VertexSettingsController, Node> controllerAndView = fxWeaver.load(VertexSettingsController.class);
            // bind controller with selected vertex
            controllerAndView.getController().bindVertex((MyVertex<Integer>)graphVertex.getUnderlyingVertex());
            // configure and show popUp
            PopOver vertexSettingsWindow = new PopOver(controllerAndView.getView().get());
            vertexSettingsWindow.show((Node)graphVertex);

            // bind vertex traitor property with vertex color
            MyVertex<Integer> vertex = (MyVertex<Integer>)graphVertex.getUnderlyingVertex();
            vertex.getIsTraitor().addListener(changed -> {
                if (vertex.getIsTraitor().get()) {
                    graphView.getStylableVertex(vertex).setStyleClass("traitor");
                } else {
                    graphView.getStylableVertex(vertex).setStyleClass("vertex");
                }
            });
        });
    }

    public void initGraph() {
        graphView.init();
    }

    // TODO: implement update as listener to graph changes
    public void update(){
        graphView.updateAndWait();
    }

    // only for test purposes
    public void addExampleVertex(){
        var vertex = graph.insertVertex(counter++);
        update();
        graphView.setVertexPosition(vertex, 100, 100);
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
