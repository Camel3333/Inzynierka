package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.model.*;
import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class KingAlgorithm implements Algorithm{
    private int phase = 0;
    private int numberOfPhases;
    private MyGraph<Integer, Integer> graph;
    private AlgorithmPhase round = AlgorithmPhase.SEND;
    private BooleanProperty isFinished = new SimpleBooleanProperty(false);

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.KING;
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
        this.graph = graph;
        numberOfPhases =  (int)settings.getSettings().get("phase").getValue();
    }

    @Override
    public StepReport step() {
        switch (round){
            case SEND -> {
                round = AlgorithmPhase.CHOOSE;
                return firstRound(graph);
            }
            case CHOOSE -> {
                phase ++;
                round = AlgorithmPhase.SEND;
                if(phase == numberOfPhases){
                    isFinished.setValue(true);
                }
                return secondRound(graph);
            }
        }
        return null;
    }

    @Override
    public boolean isFinished() {
        return phase > numberOfPhases;
    }

    @Override
    public BooleanProperty getIsFinishedProperty() {
        return isFinished;
    }

    public StepReport firstRound(MyGraph<Integer, Integer> graph){
        System.out.println("First round");
        KingStepRecord report = new KingStepRecord();
        report.fillRoles(null);
        for(Vertex<Integer> v : graph.vertices()){
            for(Vertex<Integer> u : graph.vertices()){
                BooleanProperty opinion = ((MyVertex<Integer>) v).getNextOpinion((MyVertex<Integer>) u);
                ((MyVertex<Integer>) u).receiveOpinion(opinion);
                report.getOperations().add(new SendOperation(v.element(), u.element(), opinion));
            }
        }
        return report;
    }

    public StepReport secondRound(MyGraph<Integer, Integer> graph){
        System.out.println("Second round");
        KingStepRecord report = new KingStepRecord();
        MyVertex<Integer> king = (MyVertex<Integer>) graph.vertices().stream().toList().get(phase % graph.numVertices());
        report.fillRoles(king);
        int condition = graph.numVertices() / 2 + graph.getTraitorsCount();
        for(Vertex<Integer> v : graph.vertices()){
            ((MyVertex<Integer>) v).chooseMajorityWithTieBreaker(king.getNextOpinion((MyVertex<Integer>) v), condition);
            report.getOperations().add(new ChooseOperation(v.element(), ((MyVertex<Integer>) v).getForAttack()));
        }
        return report;
    }

    private enum AlgorithmPhase{
        SEND,
        CHOOSE,
    }

    private class KingStepRecord extends StepReport{
        public void fillRoles(Vertex<Integer> king){
            for(Vertex<Integer> v : graph.vertices()){
                if(v.equals(king)){
                    getRoles().put(v, VertexRole.KING);
                }
                else{
                    getRoles().put(v, VertexRole.LIEUTENANT);
                }
            }
        }
    }

}
