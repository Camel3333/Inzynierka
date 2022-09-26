package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.OperationsBatch;
import com.example.algorithm.report.StepReport;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;


public class KingAlgorithm implements Algorithm {
    private int phase = 0;
    private int numberOfPhases;
    private MyGraph<Integer, Integer> graph;
    private AlgorithmPhase round = AlgorithmPhase.SEND;

    @Getter
    private final BooleanProperty isFinished = new SimpleBooleanProperty(false);

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.KING;
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
        this.graph = graph;
        numberOfPhases = (int) settings.getSettings().get("phase").getValue();
    }

    @Override
    public StepReport step() {
        switch (round) {
            case SEND -> {
                round = AlgorithmPhase.CHOOSE;
                return firstRound();
            }
            case CHOOSE -> {
                round = AlgorithmPhase.SEND;
                phase++;
                checkIsFinished();
                return secondRound();
            }
        }
        return null;
    }

    @Override
    public boolean isFinished() {
        return isFinished.get();
    }

    @Override
    public BooleanProperty getIsFinishedProperty() {
        return isFinished;
    }

    private void checkIsFinished() {
        if (phase == numberOfPhases) {
            isFinished.setValue(true);
        }
    }

    public StepReport firstRound() {
        System.out.println("First round");
        KingStepReport report = new KingStepReport();
        report.fillRoles(null);
        OperationsBatch operationsBatch = new OperationsBatch();
        for (Vertex<Integer> v : graph.vertices()) {
            for (Vertex<Integer> u : graph.vertexNeighbours(v)) {
                BooleanProperty opinion = ((MyVertex<Integer>) v).getNextOpinion((MyVertex<Integer>) u);
                ((MyVertex<Integer>) u).receiveOpinion(opinion);
                operationsBatch.add(new SendOperation(v, u, opinion));
            }
        }
        report.addBatch(operationsBatch);
        report.setNumSupporting(graph.getSupportingOpinionCount());
        report.setNumNotSupporting(graph.getNotSupportingOpinionCount());
        return report;
    }

    public StepReport secondRound() {
        System.out.println("Second round");
        KingStepReport report = new KingStepReport();
        MyVertex<Integer> king = (MyVertex<Integer>) graph.vertices().stream().toList().get(phase % graph.numVertices());
        report.fillRoles(king);
        int condition = graph.numVertices() / 2 + graph.getTraitorsCount();
        OperationsBatch operationsBatch = new OperationsBatch();
        for (Vertex<Integer> v : graph.vertices()) {
            ((MyVertex<Integer>) v).chooseMajorityWithTieBreaker(king.getNextOpinion((MyVertex<Integer>) v), condition);
            operationsBatch.add(new ChooseOperation(v, ((MyVertex<Integer>) v).getIsSupporting()));
        }
        report.addBatch(operationsBatch);
        report.setNumSupporting(graph.getSupportingOpinionCount());
        report.setNumNotSupporting(graph.getNotSupportingOpinionCount());
        return report;
    }

    private class KingStepReport extends StepReport {
        public void fillRoles(Vertex<Integer> king) {
            for (Vertex<Integer> v : graph.vertices()) {
                if (v.equals(king)) {
                    getRoles().put(v, VertexRole.KING);
                } else {
                    getRoles().put(v, VertexRole.NONE);
                }
            }
        }
    }

}
