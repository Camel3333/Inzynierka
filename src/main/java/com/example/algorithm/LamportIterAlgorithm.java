package com.example.algorithm;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.model.MyGraph;
import com.example.model.MyVertex;
import com.example.settings.AlgorithmSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LamportIterAlgorithm implements Algorithm{
    private int depth;
    private Map<String, String> algorithmState = new HashMap<>();
    private Stack<StackRecord> stack = new Stack<>();

    private void om_iter(){
        var record = stack.pop();

        switch (record.phase){
            case SEND -> {
                for(Vertex<Integer> vertex : record.lieutenants){
                    if (record.m == depth){
                        ((MyVertex<Integer>) vertex).getOpinion().setIsSupporting(record.commander.isSupportingOpinion().get());
                    }
                    ((MyVertex<Integer>) vertex).receiveOpinion(record.commander.getNextOpinion((MyVertex<Integer>) vertex));
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
                }
            }
        }
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
    public List<Operation> step() {
        if (!stack.empty()){
            om_iter();
        }
        return null;
    }

    @Override
    public boolean isFinished() {
        return stack.empty();
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
