package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LamportIterAlgorithm implements Algorithm{
    private int depth;
    private Graph<Integer, Integer> graph;
    private Map<String, String> algorithmState = new HashMap<>();
    private Stack<StackRecord> stack = new Stack<>();
    private BooleanProperty isFinished = new SimpleBooleanProperty(false);

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.LAMPORT;
    }

    private StepReport om_iter(){
        var record = stack.pop();
        LamportIterStepReport stepReport = new LamportIterStepReport();

        stepReport.fillRoles(record);

        switch (record.phase){
            case SEND -> {
                for(MyVertex<Integer> vertex : record.lieutenants){
                    if (record.m == depth){
                        vertex.setForAttack(record.commander.isSupportingOpinion());
                    }
                    BooleanProperty commanderOpinion = record.commander.getNextOpinion(vertex);
                    vertex.receiveOpinion(commanderOpinion);
                    stepReport.getOperations().add(new SendOperation(record.commander, vertex, commanderOpinion));
                }

                if(record.m > 0) {
                    stack.push(new StackRecord(record.commander, record.lieutenants, record.m, AlgorithmPhase.CHOOSE));

                    for (MyVertex<Integer> vertex : record.lieutenants) {
                        List<MyVertex<Integer>> new_lieutenants = record.lieutenants.stream()
                                .filter(general -> !general.equals(vertex)).toList();
                        stack.push(new StackRecord(vertex, new_lieutenants, record.m - 1, AlgorithmPhase.SEND));
                    }
                }
            }
            case CHOOSE -> {
                for(MyVertex<Integer> vertex : record.lieutenants){
                    vertex.chooseMajority();
                    stepReport.getOperations().add(new ChooseOperation(vertex, vertex.getForAttack()));
                }
            }
        }

        return stepReport;
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
        this.graph = graph;
        stack = new Stack<>();
        MyVertex<Integer> commander = (MyVertex<Integer>) graph.vertices().stream().toList().get(0);
        depth = (int)settings.getSettings().get("depth").getValue();
        if(graph.numVertices() > 0){
            stack.push(new StackRecord(commander,
                    graph.vertexNeighbours(commander).stream().map(vertex -> (MyVertex<Integer>)vertex).toList(),
                    depth, AlgorithmPhase.SEND));
        }
    }

    @Override
    public StepReport step() {
        if (!stack.empty()){
            StepReport stepReport = om_iter();
            return stepReport;
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

    @Override
    public void checkIsFinished() {
        if (stack.empty()) {
            isFinished.set(true);
        }
    }

    private class LamportIterStepReport extends StepReport{
        public void fillRoles(StackRecord record){
            getRoles().put(record.commander, VertexRole.COMMANDER);
            record.lieutenants.forEach(vertex -> {
                getRoles().put(vertex, VertexRole.LIEUTENANT);
            });
            graph.vertices()
                    .stream()
                    .filter(vertex -> !vertex.equals(record.commander) && !record.lieutenants.contains(vertex))
                    .forEach(vertex -> getRoles().put(vertex, VertexRole.NONE));
        }
    }

    private record StackRecord(MyVertex<Integer> commander,
                               List<MyVertex<Integer>> lieutenants,
                               int m,
                               AlgorithmPhase phase){
    }

    private enum AlgorithmPhase{
        SEND,
        CHOOSE
    }
}
