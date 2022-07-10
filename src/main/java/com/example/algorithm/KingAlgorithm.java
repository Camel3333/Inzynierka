package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.settings.AlgorithmSettings;

import java.util.List;

public class KingAlgorithm implements Algorithm{
    private int phase = 0;

//    @Override
//    public void execute(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
//        int f = (int)settings.getSettings().get("phase").getValue();
//        if(graph.numVertices() == 0){
//            return;
//        }
//        for(int i = 0; i <= f; i++){
//            step(graph);
//        }
//    }

    public void step(MyGraph<Integer, Integer> graph){
        firstRound(graph);
        secondRound(graph);
        phase ++;
    }

    public void firstRound(MyGraph<Integer, Integer> graph){
        for(Vertex<Integer> v : graph.vertices()){
            for(Vertex<Integer> u : graph.vertices()){
                ((MyVertex<Integer>) u).receiveOpinion(((MyVertex<Integer>) v).getNextOpinion((MyVertex<Integer>) u));
            }
            // send all
        }
    }

    public void secondRound(MyGraph<Integer, Integer> graph){
        MyVertex<Integer> king = (MyVertex<Integer>) graph.vertices().stream().toList().get(phase % graph.numVertices());
        // suspend -> show king
        int condition = graph.numVertices() / 2 + graph.getTraitorsCount();
        for(Vertex<Integer> v : graph.vertices()){
            ((MyVertex<Integer>) v).chooseMajorityWithTieBreaker(king.getNextOpinion((MyVertex<Integer>) v), condition);
        }
        // king sent
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {

    }

    @Override
    public List<Operation> step() {
        return null;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
