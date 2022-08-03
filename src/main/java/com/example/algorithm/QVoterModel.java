package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Random;

public class QVoterModel implements Algorithm{
    private MyGraph<Integer, Integer> graph;
    private BooleanProperty isFinished = new SimpleBooleanProperty(false);
    private int q;
    private int maxTime;
    private int time = 0;

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.QVOTER;
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
        this.graph = graph;
        maxTime = (int)settings.getSettings().get("time").getValue();
        q = (int)settings.getSettings().get("q").getValue();
    }

    @Override
    public StepReport step() {
        QVoterStepRecord report = new QVoterStepRecord();
        int agentIndex = new Random().nextInt(graph.numVertices() + 1);

        MyVertex<Integer> agent = (MyVertex<Integer>) graph.vertices().stream().toList().get(agentIndex);

        List<Vertex<Integer>> neighbours = graph.vertexNeighbours(agent).stream().toList();
        Collections.shuffle(neighbours);
        neighbours = neighbours.stream().limit(q).toList();

        report.fillRoles(null); //todo

        List<Boolean> opinionsReceived = new ArrayList<>();
        for(Vertex<Integer> neighbour : neighbours){
            BooleanProperty opinion = ((MyVertex<Integer>) neighbour).getNextOpinion(agent);
            opinionsReceived.add(opinion.getValue());
            report.getOperations().add(new SendOperation(neighbour, agent, opinion));
        }

        if(opinionsReceived.stream().distinct().count() <= 1 ||  time > maxTime / 2){ //todo
            agent.setForAttack(new SimpleBooleanProperty(opinionsReceived.get(0)));
        }
        report.getOperations().add(new ChooseOperation(agent, agent.getForAttack()));
        return report;
    }

    @Override
    public boolean isFinished() {
        return isFinished.getValue();
    }

    @Override
    public BooleanProperty getIsFinishedProperty() {
        return isFinished;
    }

    private class QVoterStepRecord extends StepReport{ //todo
        public void fillRoles(Vertex<Integer> king){
            for(Vertex<Integer> v : graph.vertices()){
                if(v.equals(king)){
                    getRoles().put(v, VertexRole.AGENT);
                }
                else{
                    getRoles().put(v, VertexRole.NEIGHBOUR);
                }
            }
        }
    }
}
