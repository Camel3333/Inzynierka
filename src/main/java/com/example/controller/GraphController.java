package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.algorithm.VertexRole;
import com.example.draw.CreationHelper;
import com.example.draw.MySmartGraphPanel;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.util.DrawMouseEventHandler;
import com.example.util.GraphConverter;
import com.example.util.GraphObserver;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.PopOver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    @Autowired
    private FxWeaver fxWeaver;

    private int vertexIdCounter = 0;
    private SmartGraphDemoContainer container;
    @Getter
    private MySmartGraphPanel<Integer, Integer> graphView;

    @Getter
    private MyGraph<Integer, Integer> graph;
    private List<GraphObserver<Integer, Integer>> observers = new ArrayList<>();

    public void addObserver(GraphObserver<Integer, Integer> observer) {
        observers.add(observer);
    }

    public void removeObserver(GraphObserver<Integer, Integer> observer) {
        observers.remove(observer);
    }

    public void setModelGraph(MyGraph<Integer, Integer> graph){
        this.graph = graph;
        vertexIdCounter = graph.numVertices();

        //remove old graph
        graphRoot.getChildren().remove(container);
        init();
        initGraphView();

        colorGraphView();
    }

    private void buildGraphContainers() {
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

    public void addVertexStyle(int id, String style) {
        Platform.runLater(()->graphView.getStylableVertex(id).addStyleClass(style));
    }

    public void removeVertexStyle(int id, String style) {
        Platform.runLater(()->graphView.getStylableVertex(id).removeStyleClass(style));
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

        graph.vertices().forEach(this::addVertexListeners);
    }

    public void addVertexListeners(Vertex<Integer> vertex) {
        ((MyVertex<Integer>) vertex).getIsTraitor().addListener(changed -> {
            System.out.println("traitor changed");
            changeVertexFillStyle(vertex);
        });

        ((MyVertex<Integer>) vertex).isSupportingOpinion().addListener(changed -> {
            System.out.println("opinion changed");
            changeVertexStrokeStyle(vertex);
        });
    }

    public void colorGraphView () {
        for(Vertex<Integer> vertex : graph.vertices()) {
            colorVertex(vertex);
        }
    }

    public void colorVertex (Vertex<Integer> vertex) {
        changeVertexFillStyle(vertex);
        changeVertexStrokeStyle(vertex);
    }

    public void changeVertexFillStyle(Vertex<Integer> vertex) {
        if (((MyVertex<Integer>) vertex).getIsTraitor().get()) {
            removeVertexStyle(vertex.element(), "loyal");
            addVertexStyle(vertex.element(), "traitor");
        } else {
            removeVertexStyle(vertex.element(), "traitor");
            addVertexStyle(vertex.element(), "loyal");
        }
    }

    public void changeVertexStrokeStyle (Vertex<Integer> vertex) {
        if (((MyVertex<Integer>) vertex).isSupportingOpinion().get()) {
            removeVertexStyle(vertex.element(), "defense");
            addVertexStyle(vertex.element(), "attack");
        } else {
            removeVertexStyle(vertex.element(), "attack");
            addVertexStyle(vertex.element(), "defense");
        }
    }

    public void highlightRole (Vertex<Integer> vertex, VertexRole vertexRole) {
        for(VertexRole role : VertexRole.values()){
            removeVertexStyle(vertex.element(), role.toString().toLowerCase(Locale.ROOT));
        }
        addVertexStyle(vertex.element(), vertexRole.toString().toLowerCase(Locale.ROOT));
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

    public void exportGraph() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showSaveDialog(this.graphRoot.getScene().getWindow());
        if (file != null) {
            GraphConverter.saveGraphML(file, graph);
        }
    }

    public void importGraph() throws ParserConfigurationException, IOException, SAXException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph File");
        File graphFile = fileChooser.showOpenDialog(this.graphRoot.getScene().getWindow());
        if (graphFile != null) {
            setModelGraph(GraphConverter.fromML(graphFile));
        }
    }

    public void addNodeToView(Node node){
        graphView.getChildren().add(node);
    }

    public Point2D getVertexPosition(Vertex<Integer> vertex){
        return new Point2D(graphView.getVertexPositionX(vertex), graphView.getVertexPositionY(vertex));
    }

    public void removeNodeFromView(Node node){
        graphView.getChildren().remove(node);
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
