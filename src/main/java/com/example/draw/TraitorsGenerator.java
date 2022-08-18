package com.example.draw;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TraitorsGenerator {

    public void generateTraitors(MyGraph<Integer, Integer> graph, double percentage){
        int numberOfVerticesToBeTraitors = (int)(graph.numVertices()*percentage);
        List<Vertex<Integer>> traitors = pickTraitors(graph.vertices(), numberOfVerticesToBeTraitors);
        graph.vertices().forEach(vertex -> ((MyVertex<Integer>)vertex).setIsTraitor(traitors.contains(vertex)));
    }

    private List<Vertex<Integer>> pickTraitors(Collection<Vertex<Integer>> vertices, int numberOfTraitors){
        List<Vertex<Integer>> verticesCopy = new ArrayList<>(vertices);
        Collections.shuffle(verticesCopy);
        return verticesCopy.subList(0, numberOfTraitors);
    }
}
