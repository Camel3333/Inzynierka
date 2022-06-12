package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;

import java.util.List;
import java.util.stream.Collectors;


public class LamportAlgorithm implements Algorithm{
    public void execute(MyGraph<Integer, Integer> myGraph, int depth){
        if(myGraph.numVertices() == 0){ //?
            return;
        }
        MyVertex<Integer> commander = (MyVertex<Integer>) myGraph.vertices().stream().toList().get(0); //rand/chosen?
        om(commander, (List<Vertex<Integer>>) myGraph.vertexNeighbours(commander), depth);
    }

    private void om(MyVertex<Integer> commander, List<Vertex<Integer>> lieutenants, int m){
        for(Vertex<Integer> vertex : lieutenants){
            ((MyVertex<Integer>) vertex).receiveOpinion(commander.getNextOpinion((MyVertex<Integer>) vertex));
        }
        if(m > 0){
            for(Vertex<Integer> vertex : lieutenants){
                List<Vertex<Integer>> new_lieutenants = lieutenants.stream()
                        .filter(general -> !general.equals(vertex))
                        .collect(Collectors.toList());
                om((MyVertex<Integer>) vertex, new_lieutenants, m - 1);
            }
            for(Vertex<Integer> vertex : lieutenants){
                ((MyVertex<Integer>) vertex).chooseMajority();
            }
        }
    }
}
