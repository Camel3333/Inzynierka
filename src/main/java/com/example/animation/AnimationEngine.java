package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.OperationType;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.animation.choose.ChooseAnimationFactory;
import com.example.animation.send.SendAnimationFactory;
import com.example.controller.GraphController;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public abstract class AnimationEngine {
    @Setter
    protected GraphController graphController;
    private SendAnimationFactory sendAnimationFactory = new SendAnimationFactory();
    private ChooseAnimationFactory chooseAnimationFactory = new ChooseAnimationFactory();

    public AnimationEngine(GraphController graphController) {
        this.graphController = graphController;
    }

    public void animate(StepReport report) {
        highlightRoles(report.getRoles());
        Map<OperationType, List<Operation>> operationsPerType = groupOperationsByType(report.getOperations());
        List<List<Animation>> animationsPertType = operationsPerType.entrySet()
                .stream()
                .map(e -> getAnimationsPerOperationType(e.getKey(), e.getValue()))
                .toList();
        animationsPertType.forEach(this::animateConcurrently);
    }

    protected abstract void highlightRoles(Map<Vertex<Integer>, VertexRole> roles);

    private Map<OperationType, List<Operation>> groupOperationsByType(List<Operation> operations) {
        return operations.stream().collect(Collectors.groupingBy(Operation::getType));
    }

    public void animateSend(SendOperation operation) {
        List<Animation> sendAnimations = getAnimationsPerOperationType(OperationType.SEND, List.of(operation));
        animateConcurrently(sendAnimations);
    }

    public void animateOpinionChange(ChooseOperation operation) {
        List<Animation> chooseAnimations = getAnimationsPerOperationType(OperationType.CHOOSE, List.of(operation));
        animateConcurrently(chooseAnimations);
    }

    private List<Animation> getAnimationsPerOperationType(OperationType operationType, List<Operation> operations) {
        switch (operationType) {
            case SEND -> {
                return operations
                        .stream()
                        .map(operation -> (SendOperation) operation)
                        .map(sendOperation -> {
                            Point2D fromPosition = graphController.getVertexPosition(sendOperation.getFrom());
                            Point2D toPosition = graphController.getVertexPosition(sendOperation.getTo());
                            return getSendAnimation(fromPosition, toPosition, sendOperation.getSentOpinion().get());
                        })
                        .toList();
            }
            case CHOOSE -> {
                return operations
                        .stream()
                        .map(operation -> (ChooseOperation) operation)
                        .map(chooseOperation -> getChooseOpinionAnimation((SmartGraphVertexNode<Integer>) graphController.getGraphView().getStylableVertex(chooseOperation.getVertex().element()), chooseOperation.getChosenOpinion().get()))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    private Animation getSendAnimation(Point2D from, Point2D to, boolean attack) {

        PathTransition animation = attack ? sendAnimationFactory.getAttackAnimation(from, to) : sendAnimationFactory.getDefenseAnimation(from, to);

        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> {
            try {
                graphController.addNodeToView(animation.getNode());
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        animation.setOnFinished(
                e -> Platform.runLater(() -> graphController.removeNodeFromView(animation.getNode())));

        return animation;
    }

    private Animation getChooseOpinionAnimation(SmartGraphVertexNode<Integer> vertex, boolean attack) {
        String vertexStyle = attack ? "attack" : "defense";
        return chooseAnimationFactory.getChooseOpinionAnimation(vertex, e -> graphController.addVertexStyle(vertex.getUnderlyingVertex().element(), vertexStyle));
    }

    public void animateConcurrently(List<Animation> animations) {
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(animations);
        runAnimation(parallelTransition);
    }

    private void runAnimation(Animation animation) {
        Semaphore semaphore = new Semaphore(0);

        animation.setOnFinished(e -> {
            semaphore.release();
        });

        animation.play();

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
