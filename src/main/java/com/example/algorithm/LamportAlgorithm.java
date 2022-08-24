package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.report.StepReport;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;

import java.util.List;
import java.util.stream.Collectors;


public class LamportAlgorithm implements Algorithm{
    private int depth;
    private boolean isFinished = false;

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.LAMPORT;
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
        depth = (int)settings.getSettings().get("depth").getValue();
        MyVertex<Integer> commander = (MyVertex<Integer>) graph.vertices().stream().toList().get(0);
        Thread thread = new Thread() {
            public void run(){
                try {
                    om(commander, (List<Vertex<Integer>>) graph.vertexNeighbours(commander), depth);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public synchronized StepReport step(){
        notify();
        return null;
    }

    private synchronized void om(MyVertex<Integer> commander, List<Vertex<Integer>> lieutenants, int m) throws InterruptedException {
        if(m == depth){
            wait();
        }
        for(Vertex<Integer> vertex : lieutenants){
            if (m == depth){
                ((MyVertex<Integer>) vertex).setIsSupporting(commander.isSupportingOpinion().getValue());
            }
            ((MyVertex<Integer>) vertex).receiveOpinion(commander.getNextOpinion((MyVertex<Integer>) vertex));
        }
        wait();
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
            wait();
        }
        if(m == depth){
            isFinished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public BooleanProperty getIsFinishedProperty() {
        return null;
    }

    @Override
    public void checkIsFinished() {

    }
}
