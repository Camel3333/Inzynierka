package com.example.controller;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.example.draw.CreationHelper;
import com.example.draw.MySmartGraphPanel;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.util.DrawMouseEventHandler;
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

    private void updateCss() {
        for (Vertex<Integer> vertex : this.graph.vertices()) {
            if (((MyVertex<Integer>)vertex).getIsTraitor().get()) {
                graphView.getStylableVertex(vertex).setStyleClass("traitor");
            } else {
                graphView.getStylableVertex(vertex).setStyleClass("vertex");
            }

            if (((MyVertex<Integer>)vertex).getForAttack().get()) {
                graphView.getStylableVertex(vertex).addStyleClass("attack");
            } else {
                graphView.getStylableVertex(vertex).addStyleClass("defense");
            }
        }
    }

    public void setModelGraph(MyGraph<Integer, Integer> graph){
        this.graph = graph;
        vertexIdCounter = graph.numVertices();

        //remove old graph
        graphRoot.getChildren().remove(container);
        init();
        initGraphView();
        this.updateCss();
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

    public void setVertexStyle(int id, String style) {
        Platform.runLater(()->graphView.getStylableVertex(id).setStyleClass(style));
    }

    public void addVertexStyle(int id, String style) {
        Platform.runLater(()->graphView.getStylableVertex(id).addStyleClass(style));
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

    private void saveGraphML(File file) throws IOException {
        String header = """
                <?xml version="1.0" encoding="UTF-8"?>
                <graphml xmlns="http://graphml.graphdrawing.org/xmlns"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns
                http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
                """;
        String attrs = """
                <key id="d0" for="node" attr.name="attack" attr.type="boolean"><default>true</default></key>
                <key id="d1" for="node" attr.name="traitor" attr.type="boolean"><default>false</default></key>
                """;

        String graph = "<graph id=\"G\" edgedefault=\"undirected\">\n";
        StringBuilder edgesString = new StringBuilder();
        int i = 0;
        for (Edge<Integer, Integer> edge: this.graph.edges()) {
            edgesString.append("<edge id=\"").append(i).append("\" source=\"").append(edge.vertices()[0].element()).append("\" target=\"").append(edge.vertices()[1].element()).append("\"></edge>").append("\n");
            i++;
        }
        StringBuilder verticesString = new StringBuilder();
        for (Vertex<Integer> vertex: this.graph.vertices()) {
            boolean isFor = ((MyVertex<Integer>)vertex).isSupportingOpinion().get();
            boolean isTraitor = ((MyVertex<Integer>)vertex).isTraitor().get();
            verticesString.append("<node id=\"").append(vertex.element()).append("\"><data key=\"d0\">").append(isFor).append("</data><data key=\"d1\">").append(isTraitor).append("</data></node>").append("\n");
        }
        String finish = "</graph></graphml>\n";

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(header+attrs+graph+edgesString+verticesString+finish);
        writer.close();
        System.out.println("export done");
    }

    public void exportGraph() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showSaveDialog(this.graphRoot.getScene().getWindow());
        if (file != null) {
            this.saveGraphML(file);
        }
    }

    private void fromML(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        Map<String, String> keys = new HashMap<>();
        List<MyVertex<Integer>> vertices = new LinkedList<>();
        List<Pair<Integer, Integer>> edges = new LinkedList<>();

        // Get data keys
        NodeList keyNodes = doc.getElementsByTagName("key");
        for (int i = 0; i < keyNodes.getLength(); i++) {
            org.w3c.dom.Node node = keyNodes.item(i);
            String id = node.getAttributes().getNamedItem("id").getNodeValue();
            keys.put(id,
                    node.getAttributes().getNamedItem("attr.name").getNodeValue());
        }

        // Get vertices
        NodeList vertexNodes = doc.getElementsByTagName("node");
        for (int i = 0; i < vertexNodes.getLength(); i++) {
            org.w3c.dom.Node node = vertexNodes.item(i);
            MyVertex<Integer> newVertex = new MyVertex<>(Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
            boolean traitor = false; //todo default value from graphML
            boolean opinion = false;

            NodeList dataNodes = node.getChildNodes();
            for (int j = 0; j < dataNodes.getLength(); j++) {
                org.w3c.dom.Node dataNode = dataNodes.item(j);
                String key = dataNode.getAttributes().getNamedItem("key").getNodeValue();
                String attribute = keys.get(key);
                if (key != null) {
                    switch (attribute) {
                        case "traitor" -> traitor = Boolean.parseBoolean(dataNode.getChildNodes().item(0).getNodeValue());
                        case "attack" -> opinion = Boolean.parseBoolean(dataNode.getChildNodes().item(0).getNodeValue());
                    }

                }
            }
            newVertex.setIsTraitor(traitor);
            newVertex.setForAttack(new SimpleBooleanProperty(opinion));
            vertices.add(newVertex);
        }

        //Get edges
        NodeList edgeNodes = doc.getElementsByTagName("edge");
        for (int i = 0; i < edgeNodes.getLength(); i++) {
            org.w3c.dom.Node node = edgeNodes.item(i);
            int from = Integer.parseInt(node.getAttributes().getNamedItem("source").getNodeValue());
            int to = Integer.parseInt(node.getAttributes().getNamedItem("target").getNodeValue());
            edges.add(new Pair<>(from, to));
        }

        //Reset graph
        MyGraph<Integer, Integer> newGraph = new MyGraph<>();
        int newCounter = 0;
        for (MyVertex<Integer> vertex : vertices) {
            newGraph.insertVertex(vertex);
            vertexIdCounter = Integer.max(vertex.element(), vertexIdCounter);
        }

        for (Pair<Integer, Integer> edge : edges) {
            newGraph.insertEdge(edge.getKey(), edge.getValue(), 1);
        }

        this.setModelGraph(newGraph);
        vertexIdCounter = newCounter;
        System.out.println("import done");
    }

    public void importGraph() throws ParserConfigurationException, IOException, SAXException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph File");
        File graphFile = fileChooser.showOpenDialog(this.graphRoot.getScene().getWindow());
        if (graphFile != null) {
            this.fromML(graphFile);
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
