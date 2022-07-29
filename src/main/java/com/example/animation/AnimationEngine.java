package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.OperationType;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.controller.GraphController;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public abstract class AnimationEngine{
    protected GraphController graphController;
    private Duration sendDuration = new Duration(1000);

    public AnimationEngine(GraphController graphController){
        this.graphController = graphController;
    }

    public void animate(StepReport report){
        highlightRoles(report.getRoles());

        // group animations by type
        Map<OperationType, List<Operation>> operationsPerType = report.getOperations().stream().collect(Collectors.groupingBy(Operation::getType));

        operationsPerType.entrySet().forEach(entry -> {
            animateConcurrently(entry.getKey(), entry.getValue());
        });
    }

    protected abstract void highlightRoles(Map<Vertex<Integer>, VertexRole> roles);

    public void animateSend(SendOperation operation){
        // unpack send operation and animate
        graphController.sendMessage(operation.getFromId(), operation.getToId());
    }

    public void animateOpinionChange(ChooseOperation operation){
        // unpack opinion change operation and animate
//        System.out.println("Animating opinion change for "+operation.getId()+" to "+operation.getChosenOpinion().getValue());
    }

    public void animateConcurrently(OperationType type, List<Operation> operations){
        switch (type){
            case SEND -> {
                ParallelTransition parallelTransition = new ParallelTransition();

                List<PathTransition> animations = (operations
                        .stream()
                        .map(operation -> graphController.getSendTransition(((SendOperation)operation).getFromId(), ((SendOperation)operation).getToId()))
                        .toList());

                // set duration for each animation
                animations.forEach(animation -> animation.setDuration(sendDuration));

                parallelTransition.getChildren().addAll(animations);

                Semaphore semaphore = new Semaphore(0);

                parallelTransition.setOnFinished(e -> {
                    System.out.println("All animations finished");
                    semaphore.release();
                });

                parallelTransition.play();

                try {
                    // wait until all animations will finish
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
