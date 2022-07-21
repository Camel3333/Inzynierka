package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.model.AgentOpinion;
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
                for(Vertex<Integer> vertex : record.lieutenants){
                    if (record.m == depth){
                        ((MyVertex<Integer>) vertex).getOpinion().setIsSupporting(record.commander.isSupportingOpinion().get());
                    }
                    AgentOpinion commanderOpinion = record.commander.getNextOpinion((MyVertex<Integer>) vertex);
                    ((MyVertex<Integer>) vertex).receiveOpinion(commanderOpinion);
                    stepReport.getOperations().add(new SendOperation(record.commander.element(), vertex.element(), commanderOpinion));
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
                for(Vertex<Integer> vertex : record.lieutenants){
                    ((MyVertex<Integer>) vertex).chooseMajority();
                    stepReport.getOperations().add(new ChooseOperation(vertex.element(), ((MyVertex<Integer>) vertex).getOpinion()));
                }
            }
        }

        return stepReport;
    }

    @Override
    public void loadEnvironment(MyGraph<Integer, Integer> graph, AlgorithmSettings settings) {
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
            if (stack.empty()) {
                isFinished.set(true);
            }
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

//    private StepReport buildReport(StackRecord record){
//        StepReport report = new StepReport();
//
//        // set roles
//        report.getRoles().put(record.commander, VertexRole.COMMANDER);
//        for (Vertex<Integer> vertex : record.lieutenants){
//            report.getRoles().put(vertex, VertexRole.LIEUTENANT);
//        }
//
//        // fill operations
//        switch (record.phase){
//            case CHOOSE -> {
//                for(Vertex<Integer> vertex : record.lieutenants){
//                    report.getOperations().add(vertex.element(), new ChooseOperation(vertex.element(), ((MyVertex<Integer>)vertex).getOpinion()));
//                }
//            }
//            case SEND -> {
//                for (Vertex<Integer> vertex : record.lieutenants){
//                    report.getOperations().add(vertex.element(), new SendOperation(record.commander.element(), vertex.element(), ));
//                }
//            }
//        }
//    }

    private class LamportIterStepReport extends StepReport{
        public void fillRoles(StackRecord record){
            getRoles().put(record.commander, VertexRole.COMMANDER);
            for (Vertex<Integer> vertex : record.lieutenants){
                getRoles().put(vertex, VertexRole.LIEUTENANT);
            }
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
