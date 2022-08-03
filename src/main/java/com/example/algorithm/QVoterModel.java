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

import java.util.*;

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
    public StepReport step() { //todo two phases?
        time ++;
        StepReport report = new StepReport();

        int agentIndex = new Random().nextInt(graph.numVertices() + 1);
        MyVertex<Integer> agent = (MyVertex<Integer>) graph.vertices().stream().toList().get(agentIndex);
        report.getRoles().put(agent, VertexRole.VOTER_AGENT);

        ArrayList<Vertex<Integer>> neighbours = (ArrayList<Vertex<Integer>>) graph.vertexNeighbours(agent).stream().toList();
        Collections.shuffle(Arrays.asList(neighbours)); //todo refactor
        neighbours = (ArrayList<Vertex<Integer>>) neighbours.stream().limit(q).toList();

        List<Boolean> opinionsReceived = new ArrayList<>();
        for(Vertex<Integer> neighbour : neighbours){
            BooleanProperty opinion = ((MyVertex<Integer>) neighbour).getNextOpinion(agent);
            opinionsReceived.add(opinion.getValue());
            report.getOperations().add(new SendOperation(neighbour, agent, opinion));
            report.getRoles().put(neighbour, VertexRole.NEIGHBOUR);
        }

        if(opinionsReceived.stream().distinct().count() <= 1 ||  time > maxTime / 2){ //todo Boltzmann
            agent.setForAttack(new SimpleBooleanProperty(opinionsReceived.get(0)));
        }
        report.getOperations().add(new ChooseOperation(agent, agent.getForAttack()));

        if(time == maxTime){
            isFinished.setValue(true);
        }

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
}
