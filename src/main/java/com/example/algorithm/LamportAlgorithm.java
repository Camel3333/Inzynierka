package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;

import java.util.List;
import java.util.stream.Collectors;


public class LamportAlgorithm implements Algorithm{
    private int depth;
    public void execute(MyGraph<Integer, Integer> myGraph, AlgorithmSettings settings){
        System.out.println("Entering laport algorithm");
//        int depth = (int)settings.getSettings().get("depth");
        depth = (int)settings.getSettings().get("depth").getValue();
        if(myGraph.numVertices() == 0){
            return;
        }
        MyVertex<Integer> commander = (MyVertex<Integer>) myGraph.vertices().stream().toList().get(0);
        System.out.println("Commander opinion before: "+commander.isSupportingOpinion().get()+", his id = "+commander.element());
        om(commander, (List<Vertex<Integer>>) myGraph.vertexNeighbours(commander), depth);
        System.out.println("Consensus state after algorithm: "+myGraph.checkConsensus());
        System.out.println("Commander opinion after: "+commander.isSupportingOpinion().get());
    }

    private void om(MyVertex<Integer> commander, List<Vertex<Integer>> lieutenants, int m){
        for(Vertex<Integer> vertex : lieutenants){
            if (m == depth){
                ((MyVertex<Integer>) vertex).getOpinion().setIsSupporting(commander.isSupportingOpinion().get());
            }
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
