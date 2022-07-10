//package com.example.algorithm;
//
//import com.brunomnsilva.smartgraph.graph.Vertex;
//import com.example.model.MyGraph;
//import com.example.model.MyVertex;
//import com.example.settings.AlgorithmSettings;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//public class LamportAlgorithm implements AlgorithmWithStep{
//    private int depth;
//    private AlgorithmPhase currentPhase;
//    private Map<String, String> algorithmState = new HashMap<>();
//    private Stack<StackRecord> stack = new Stack<>();
//
////    public void execute(MyGraph<Integer, Integer> myGraph, AlgorithmSettings settings){
////        System.out.println("Entering laport algorithm");
////        depth = (int)settings.getSettings().get("depth").getValue();
////        if(myGraph.numVertices() == 0){
////            return;
////        }
////        MyVertex<Integer> commander = (MyVertex<Integer>) myGraph.vertices().stream().toList().get(0);
////        System.out.println("Commander opinion before: "+commander.isSupportingOpinion().get()+", his id = "+commander.element());
////        om(commander, (List<Vertex<Integer>>) myGraph.vertexNeighbours(commander), depth);
////        System.out.println("Consensus state after algorithm: "+myGraph.checkConsensus());
////        System.out.println("Commander opinion after: "+commander.isSupportingOpinion().get());
////    }
//
//    public void execute(MyGraph<Integer, Integer> myGraph, AlgorithmSettings settings){
//        System.out.println("Entering laport algorithm");
//        depth = (int)settings.getSettings().get("depth").getValue();
//        if(myGraph.numVertices() == 0){
//            return;
//        }
//        MyVertex<Integer> commander = (MyVertex<Integer>) myGraph.vertices().stream().toList().get(0);
//        System.out.println("Commander opinion before: "+commander.isSupportingOpinion().get()+", his id = "+commander.element());
//        stack.push(new StackRecord(commander, myGraph.vertexNeighbours(commander).stream().map(vertex -> (MyVertex<Integer>)vertex).toList(), depth, AlgorithmPhase.SEND));
//        while (!stack.empty()){
//            step();
//        }
//        System.out.println("Consensus state after algorithm: "+myGraph.checkConsensus());
//        System.out.println("Commander opinion after: "+commander.isSupportingOpinion().get());
//    }
//
//    private void om(MyVertex<Integer> commander, List<Vertex<Integer>> lieutenants, int m){
//        // highlight commander
//        for(Vertex<Integer> vertex : lieutenants){
//            if (m == depth){
//                ((MyVertex<Integer>) vertex).getOpinion().setIsSupporting(commander.isSupportingOpinion().get());
//            }
//            ((MyVertex<Integer>) vertex).receiveOpinion(commander.getNextOpinion((MyVertex<Integer>) vertex));
//        }
//        // show for all
//        // suspend -> next
//        if(m > 0){
//            for(Vertex<Integer> vertex : lieutenants){
//                List<Vertex<Integer>> new_lieutenants = lieutenants.stream()
//                        .filter(general -> !general.equals(vertex))
//                        .collect(Collectors.toList());
//                om((MyVertex<Integer>) vertex, new_lieutenants, m - 1);
//            }
//            for(Vertex<Integer> vertex : lieutenants){
//                ((MyVertex<Integer>) vertex).chooseMajority();
//            }
//        }
//    }
//
//    private void om_iter(){
//        var record = stack.pop();
//
//        switch (record.phase){
//            case SEND -> {
//                for(Vertex<Integer> vertex : record.lieutenants){
//                    if (record.m == depth){
//                        ((MyVertex<Integer>) vertex).getOpinion().setIsSupporting(record.commander.isSupportingOpinion().get());
//                    }
//                    ((MyVertex<Integer>) vertex).receiveOpinion(record.commander.getNextOpinion((MyVertex<Integer>) vertex));
//                }
//
//                if(record.m > 0) {
//                    stack.push(new StackRecord(record.commander, record.lieutenants, record.m, AlgorithmPhase.CHOOSE));
//
//                    for (MyVertex<Integer> vertex : record.lieutenants) {
//                        List<MyVertex<Integer>> new_lieutenants = record.lieutenants.stream()
//                                .filter(general -> !general.equals(vertex)).toList();
//                        stack.push(new StackRecord(vertex, new_lieutenants, record.m - 1, AlgorithmPhase.SEND));
//                    }
//                }
//            }
//            case CHOOSE -> {
//                for(Vertex<Integer> vertex : record.lieutenants){
//                    ((MyVertex<Integer>) vertex).chooseMajority();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void step() {
//        if (!stack.empty()){
//            om_iter();
//        }
//    }
//
//    @Override
//    public boolean isFinished() {
//        return stack.empty();
//    }
//
//    private record StackRecord(MyVertex<Integer> commander,
//                               List<MyVertex<Integer>> lieutenants,
//                               int m,
//                               AlgorithmPhase phase){
//    }
//
//    private enum AlgorithmPhase{
//        SEND,
//        CHOOSE
//    }
//}
