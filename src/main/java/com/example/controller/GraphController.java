package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.draw.CreationHelper;
import com.example.draw.MySmartGraphPanel;
import com.example.model.MyVertex;
import com.example.util.DrawMouseEventHandler;
import com.example.util.GraphObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.PopOver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    @Getter
    private Pane graphRoot;

    @Autowired
    private FxWeaver fxWeaver;

    private int vertexIdCounter = 0;
    private SmartGraphDemoContainer container;
    @Getter
    private MySmartGraphPanel<Integer, Integer> graphView;

    @Getter
    private Graph<Integer, Integer> graph;
    private CreationHelper drawingHelper;
    private List<GraphObserver<Integer, Integer>> observers = new ArrayList<>();

    public void addObserver(GraphObserver<Integer, Integer> observer) {
        observers.add(observer);
    }

    public void removeObserver(GraphObserver<Integer, Integer> observer) {
        observers.remove(observer);
    }

    public void setModelGraph(Graph<Integer, Integer> graph){
        this.graph = graph;
        vertexIdCounter = graph.numVertices();

        //remove old graph
        graphRoot.getChildren().remove(container);
        init();
        initGraphView();
    }

    private void buildGraphContainers() {
        Vertex<Integer> v1 = graph.insertVertex(0);
        Vertex<Integer> v2 = graph.insertVertex(1);
        Vertex<Integer> v3 = graph.insertVertex(2);
        Vertex<Integer> v4 = graph.insertVertex(3);
        graph.insertEdge(v1, v2, 0);
        graph.insertEdge(v2, v3, 1);
        graph.insertEdge(v3, v4, 2);
        graph.insertEdge(v4, v1, 3);
        graph.insertEdge(v1, v3, 4);
        graph.insertEdge(v2, v4, 5);

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        // TODO: Load properties from file
        SmartGraphProperties properties = new SmartGraphProperties("edge.arrow = false");
        graphView = new MySmartGraphPanel<>(graph, properties, strategy);
        setGraphViewBindings();
        container = new SmartGraphDemoContainer(graphView);
    }

    public void setVertexStyle(int id, String style) {
        Platform.runLater(()->graphView.getStylableVertex(id).setStyleClass(style));
    }

    private void setGraphViewBindings(){
        graphView.setVertexSingleClickAction(graphVertex -> {
            observers.forEach(observer -> observer.vertexClicked(graphVertex.getUnderlyingVertex()));
        });

        graphView.setEdgeSingleClickAction(graphEdge -> {
            observers.forEach(observer -> observer.edgeClicked(graphEdge.getUnderlyingEdge()));
        });

        graphView.setVertexDoubleClickAction(graphVertex -> {
            observers.forEach(observer -> observer.vertexDoubleClicked(graphVertex.getUnderlyingVertex()));
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

            vertex.isSupportingOpinion().addListener(changed -> {
                if (vertex.isSupportingOpinion().get()) {
                    graphView.getStylableVertex(vertex).addStyleClass("attack");
                } else {
                    graphView.getStylableVertex(vertex).addStyleClass("defense");
                }
            });
        });

        graphView.setEdgeDoubleClickAction(graphEdge -> {
            observers.forEach(observer -> observer.edgeDoubleClicked(graphEdge.getUnderlyingEdge()));
        });

        DrawMouseEventHandler drawMouseEventHandler = new DrawMouseEventHandler();
        drawMouseEventHandler.setOnClickedEventHandler((mouseEvent) -> {
            System.out.println("X = "+mouseEvent.getX());
            System.out.println("Y = "+mouseEvent.getY());
            var x = mouseEvent.getX();
            var y = mouseEvent.getY();
            observers.forEach(observer -> observer.clickedAt(x,y));
        });
        graphView.addEventHandler(MouseEvent.ANY, drawMouseEventHandler);
    }

    public void setVertexPosition(Vertex vertex, double x, double y) {
        graphView.setVertexPosition(vertex, x, y);
    }

    public void initGraphView() {
        if (graphView.getAbleToInit().get()) {
            // GraphView is ready to be initialized
            graphView.init();
        }
        else {
            // Listen while GraphView won't be ready
            graphView.getAbleToInit().addListener((o, oldVal, newVal) -> {
                if (newVal && !oldVal) {
                    graphView.init();
                }
            });
        }
    }

    // TODO: implement update as listener to graph changes
    public void update(){
        graphView.updateAndWait();
    }

    private void init() {
        buildGraphContainers();
        container.prefWidthProperty().bind(graphRoot.widthProperty());
        container.prefHeightProperty().bind(graphRoot.heightProperty());
        graphRoot.getChildren().add(container);
    }

    public int getNextVertexId() {
        return vertexIdCounter++;
    }
}
