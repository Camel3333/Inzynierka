package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;

public class KingAlgorithm implements Algorithm{
    private int phase = 0;

    @Override
    public void execute(MyGraph<Integer, Integer> graph, int f) { // depth == phase ???
        for(int i = 0; i <= f; i++){
            step(graph);
        }
    }

    public void step(MyGraph<Integer, Integer> graph){
        firstRound(graph);
        secondRound(graph);
        phase ++;
    }

    public void firstRound(MyGraph<Integer, Integer> graph){
        for(Vertex<Integer> v : graph.vertices()){
            for(Vertex<Integer> u : graph.vertices()){
                ((MyVertex<Integer>) v).sendOpinions((MyVertex<Integer>) u);
            }
        }
    }

    public void secondRound(MyGraph<Integer, Integer> graph){ // king == phase
        MyVertex<Integer> king = (MyVertex<Integer>) graph.vertices().stream().toList().get(phase % graph.numVertices());
        //add geNumOfTraitors?
        int condition = graph.numVertices() / 2 + (int) graph.vertices().stream().filter(v -> ((MyVertex<Integer>) v).isTraitor().getValue()).count();
        for(Vertex<Integer> v : graph.vertices()){
            ((MyVertex<Integer>) v).decide(king.getOpinions(), condition);
        }
    }

}
