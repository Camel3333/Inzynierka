package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.OperationType;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.controller.GraphController;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public abstract class AnimationEngine{
    protected GraphController graphController;

    public void animate(StepReport report){
        highlightRoles(report.getRoles());
        Map<OperationType, List<Operation>> operationsPerType = report.getOperations().stream().collect(Collectors.groupingBy(Operation::getType));
//        for (Operation operation : report.getOperations()){
//            switch (operation.getType()){
//                case SEND -> animateSend((SendOperation) operation);
//                case CHOOSE -> animateOpinionChange((ChooseOperation) operation);
//            }
//        }
        operationsPerType.entrySet().stream().forEach(entry -> {
            animateConcurrently(entry.getKey(), entry.getValue());
        });
    }

    protected abstract void highlightRoles(Map<Vertex<Integer>, VertexRole> roles);

    public void animateSend(SendOperation operation){
        // unpack send operation and animate
        System.out.println("Animating send between "+operation.getFromId()+" and "+operation.getToId()+" message: "+operation.getSentOpinion().getValue());
        graphController.sendMessage(operation.getFromId(), operation.getToId());

    }

    public void animateOpinionChange(ChooseOperation operation){
        // unpack opinion change operation and animate
        System.out.println("Animating opinion change for "+operation.getId()+" to "+operation.getChosenOpinion().getValue());
    }

    public void animateConcurrently(OperationType type, List<Operation> operations){
        switch (type){
            case SEND -> {
                List<Thread> animations = new ArrayList<>();
                operations.stream().forEach(operation -> {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Starting send animation for thread "+Thread.currentThread().getId());
                            animateSend((SendOperation) operation);
                            System.out.println("Finished send animation for thread "+Thread.currentThread().getId());
                        }
                    });
                    animations.add(thread);
                    thread.start();
                });
                for (Thread animation : animations) {
                    try {
                        animation.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            case CHOOSE -> {
                List<Thread> animations = new ArrayList<>();
                operations.stream().forEach(operation -> {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            animateOpinionChange((ChooseOperation) operation);
                        }
                    });
                    animations.add(thread);
                    thread.start();
                });
                for (Thread animation : animations) {
                    try {
                        animation.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }
}
