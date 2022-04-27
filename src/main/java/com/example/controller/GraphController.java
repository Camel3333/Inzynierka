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
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FxmlView("/view/graphView.fxml")
public class GraphController {

    @FXML
    private Pane graphRoot;

    private SmartGraphDemoContainer container;
    private SmartGraphPanel graphView;
    private Graph<Integer, Integer> graph = new GraphEdgeList();
    private AgentGraph agentGraph = new AgentGraph();
    private HashMap<Integer, Vertex<Integer>> vertexMap = new HashMap<>();

    public GraphController() {
        agentGraph.getVertices().addListener((ListChangeListener.Change<? extends AgentVertex> change) -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for(AgentVertex agentVertex : change.getRemoved()) {
                        graph.removeVertex(vertexMap.get(agentVertex.getId()));
                        vertexMap.remove(agentVertex.getId());
                    }
                }
                else if(change.wasAdded()){
                    for(AgentVertex agentVertex : change.getAddedSubList()) {
                        Vertex<Integer> vertex = graph.insertVertex(agentVertex.getId());
                        vertexMap.put(agentVertex.getId(), vertex);
                        bindEdges(agentVertex);
                    }
                }
            }
        });

        AgentVertex agentVertex1 = new AgentVertex(1);
        agentGraph.addVertex(agentVertex1);
        AgentVertex agentVertex2 = new AgentVertex(2);
        agentGraph.addVertex(agentVertex2);

        agentVertex2.addNeighbour(agentVertex1);

        agentGraph.removeVertex(agentVertex1);

//        agentVertex2.removeNeighbour(agentVertex1);

//        graph.insertEdge(agentVertex1.getId(), agentVertex2.getId(), 1);


//        agentGraph.removeVertex(agentVertex2);
    }

    private void bindEdges(AgentVertex agent){
        agent.getNeighbours().addListener((ListChangeListener.Change<? extends AgentVertex> change) -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for(AgentVertex neighbour : change.getRemoved()) {
                        List<Edge<Integer, Integer>> edgesToRemove = graph.edges()
                                .stream()
                                .filter(edge -> {
                                    if (Arrays.stream(edge.vertices()).toList()
                                            .containsAll(Arrays.asList(vertexMap.get(agent.getId())
                                                    , vertexMap.get(neighbour.getId()))))
                                        return true;
                                    return false;
                                }).toList();
                        edgesToRemove.forEach(edge -> {
                            graph.removeEdge(edge);
                        });
                    }
                }
                else if(change.wasAdded()){
                    for(AgentVertex neighbour : change.getAddedSubList()) {
                        if (!graph.areAdjacent(vertexMap.get(agent.getId()), vertexMap.get(neighbour.getId())))
                            graph.insertEdge(vertexMap.get(agent.getId()), vertexMap.get(neighbour.getId()), 1);
                    }
                }
            }
        });
    }

    private void buildGraph() {
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
