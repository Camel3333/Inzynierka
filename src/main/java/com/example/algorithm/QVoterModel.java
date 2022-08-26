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
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class QVoterModel implements Algorithm{
    private MyGraph<Integer, Integer> graph;
    private MyVertex<Integer> selectedAgent;
    private List<Boolean> opinionsReceived;
    private QVoterModel.AlgorithmPhase algorithmPhase = QVoterModel.AlgorithmPhase.SEND;
    private ProbabilityType probabilityType = ProbabilityType.LINEAR;
    private int q;
    private int maxTime;
    private int time = 0;

    @Getter
    private final BooleanProperty isFinished = new SimpleBooleanProperty(false);

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
        switch (algorithmPhase){
            case SEND -> {
                algorithmPhase = QVoterModel.AlgorithmPhase.CHOOSE;
                return sendOpinions();
            }
            case CHOOSE -> {
                algorithmPhase = QVoterModel.AlgorithmPhase.SEND;
                time ++;
                checkIsFinished();
                return makeDecision();
            }
        }
        return null;
    }

    private StepReport sendOpinions() {
        QVoterStepReport report = new QVoterStepReport();

        int agentIndex = new Random().nextInt(graph.numVertices());
        selectedAgent = (MyVertex<Integer>) graph.vertices().stream().toList().get(agentIndex);
        List<Vertex<Integer>> agentNeighbours = getNeighbours(selectedAgent);
        report.fillRoles(selectedAgent, agentNeighbours);

        opinionsReceived = new ArrayList<>();
        for(Vertex<Integer> neighbour : agentNeighbours){
            BooleanProperty opinion = ((MyVertex<Integer>) neighbour).getNextOpinion(selectedAgent);
            opinionsReceived.add(opinion.getValue());
            report.getOperations().add(new SendOperation(neighbour, selectedAgent, opinion));
        }

        return report;
    }

    private StepReport makeDecision() {
        QVoterStepReport report = new QVoterStepReport();

        report.fillRoles(selectedAgent, null);

        if(shouldAcceptNeighboursOpinion()){
            selectedAgent.setIsSupporting(opinionsReceived.get(0));
        }
        report.getOperations().add(new ChooseOperation(selectedAgent, selectedAgent.getIsSupporting()));

        return report;
    }

    private boolean shouldAcceptNeighboursOpinion(){
        return opinionsReceived.stream().distinct().count() <= 1 || checkProbability();
    }

    @Override
    public boolean isFinished() {
        return isFinished.getValue();
    }

    @Override
    public BooleanProperty getIsFinishedProperty() {
        return isFinished;
    }

    private void checkIsFinished() {
        if(time == maxTime){
            isFinished.setValue(true);
        }
    }

    private List<Vertex<Integer>> getNeighbours(Vertex<Integer> vertex){
        Random rand = new Random();
        List<Vertex<Integer>> neighbours = new ArrayList<>(graph.vertexNeighbours(vertex));
        List<Vertex<Integer>> selectedNeighbours = new ArrayList<>();
        for (int i = 0; i < q; i++) {
            int randomIndex = rand.nextInt(neighbours.size());
            selectedNeighbours.add(neighbours.remove(randomIndex));
        }
        return selectedNeighbours;
    }

    private boolean checkProbability(){
        switch (probabilityType){
            case LINEAR -> {
                return new Random().nextInt(maxTime) <= time;
            }
        }
       return false;
    }

    private enum AlgorithmPhase{
        SEND,
        CHOOSE,
    }

    private enum ProbabilityType {
        LINEAR,
        NON_LINEAR,
        BOLTZMANN
    }

    private class QVoterStepReport extends StepReport{
        public void fillRoles(Vertex<Integer> agent, List<Vertex<Integer>> neighbours){
            for(Vertex<Integer> v : graph.vertices()){
                if(v.equals(agent)){
                    getRoles().put(v, VertexRole.VOTER);
                }
                else if(neighbours != null && neighbours.contains(v)){
                    getRoles().put(v, VertexRole.NEIGHBOUR);
                }
                else{
                    getRoles().put(v, VertexRole.NONE);
                }
            }
        }
    }
}
