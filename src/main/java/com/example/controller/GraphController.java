package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.draw.CreationHelper;
import com.example.draw.DrawMode;
import com.example.draw.MySmartGraphPanel;
import com.example.model.MyVertex;
import com.example.util.DrawMouseEventHandler;
import com.example.util.GraphObserver;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.PopOver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    @Autowired
    private FxWeaver fxWeaver;

    private int vertexIdCounter = 0;
    private SmartGraphDemoContainer container;
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

    public Graph<Integer,Integer> getModelGraph(){
        return graph;
    }

    private void buildGraphContainers() {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        // TODO: Load properties from file
        SmartGraphProperties properties = new SmartGraphProperties("edge.arrow = false");
        graphView = new MySmartGraphPanel<>(graph, properties, strategy);
        setGraphViewBindings();
        container = new SmartGraphDemoContainer(graphView);
    }

    public void setGeneral(int id) {
        graphView.getStylableVertex(id).setStyleClass("general");
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

    public void sendMessage(int v1, int v2) {
            System.out.println("DEBUG sending mesg");
            MyVertex<Integer> commander = (MyVertex<Integer>) graph.vertices().stream().toList().get(v1);
            MyVertex<Integer> commander1 = (MyVertex<Integer>) graph.vertices().stream().toList().get(v2);

            double pos1 = graphView.getVertexPositionX(commander);
            double pos2 = graphView.getVertexPositionY(commander);
            double pos2_1 = graphView.getVertexPositionX(commander1);
            double pos2_2 = graphView.getVertexPositionY(commander1);
            ImageView ball = new ImageView(new Image("file:src/main/resources/ms.jpg", 20, 20, false, false));
            ball.setX(pos1);
            ball.setY(pos2);

            try {
                ((Pane) (this.graphRoot.getChildren().stream().toList().get(0)))
                        .getChildren().add(ball);
            }
            catch (Exception e) {
                e.printStackTrace();
        }

            Path path = new Path();
            path.getElements().add(new MoveTo(pos1,pos2));
            path.getElements().add(new LineTo(pos2_1, pos2_2));

            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(1000));
            pathTransition.setNode(ball);
            pathTransition.setPath(path);

            System.out.println("DEBUG sending mesg2");
            pathTransition.play();
            pathTransition.setOnFinished(
                    e -> ((Pane)(this.graphRoot.getChildren().stream().toList().get(0))).getChildren().remove(ball)
            );

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
