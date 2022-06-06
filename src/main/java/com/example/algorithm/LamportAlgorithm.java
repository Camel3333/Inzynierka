package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;


public class LamportAlgorithm implements Algorithm{

    public void execute(MyGraph<Integer, Integer> myGraph, int depth){
        if(myGraph.numVertices() == 0){
            return;
        }
        MyVertex<Integer> commander = (MyVertex<Integer>) myGraph.vertices().stream().findFirst().get();
        om(myGraph, commander, null, depth);
    }

    private void om(MyGraph<Integer, Integer> myGraph, MyVertex<Integer> currentCommander, MyVertex<Integer> previousCommander, int depth){
        for(Vertex<Integer> vertex : myGraph.vertexNeighbours(currentCommander)){
            if(!vertex.equals(previousCommander)){
                currentCommander.sendOpinions((MyVertex<Integer>) vertex);
            }
        }
        if(depth > 0){
            for(Vertex<Integer> vertex : myGraph.vertexNeighbours(currentCommander)){
                if(!vertex.equals(previousCommander)){
                    om(myGraph, (MyVertex<Integer>) vertex, currentCommander, depth - 1);
                }
            }
        }
        for(Vertex<Integer> vertex : myGraph.vertexNeighbours(currentCommander)){
            if(!vertex.equals(previousCommander)){
                ((MyVertex<Integer>) vertex).chooseMajority();
            }
        }
    }
}
