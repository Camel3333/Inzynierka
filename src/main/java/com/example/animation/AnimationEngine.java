package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.OperationType;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.OperationsBatch;
import com.example.algorithm.report.StepReport;
import com.example.animation.choose.ChooseAnimationFactory;
import com.example.animation.send.SendAnimationFactory;
import com.example.controller.GraphController;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public class AnimationEngine {
    @Setter
    protected GraphController graphController;
    private final AnimationRunner animationRunner = new AnimationRunner();
    private SendAnimationFactory sendAnimationFactory = new SendAnimationFactory(new Duration(1500));
    private ChooseAnimationFactory chooseAnimationFactory = new ChooseAnimationFactory(new Duration(3000));

    public AnimationEngine(GraphController graphController) {
        this.graphController = graphController;
    }

    public void animate(StepReport report) {
        highlightRoles(report.getRoles());
        report.getOperationsBatches().forEach(this::animateBatch);
    }

    private void animateBatch(OperationsBatch batch){
        Map<OperationType, List<Operation>> operationsPerType = groupOperationsByType(batch.getOperations());
        List<List<Animation>> animationsPertType = operationsPerType.entrySet()
                .stream()
                .map(e -> getAnimationsPerOperationType(e.getKey(), e.getValue()))
                .toList();
        animationsPertType.forEach(animationRunner::runAnimationsConcurrently);
    }

    private Map<OperationType, List<Operation>> groupOperationsByType(List<Operation> operations) {
        return operations.stream().collect(Collectors.groupingBy(Operation::getType));
    }

    private void highlightRoles(Map<Vertex<Integer>, VertexRole> roles) {
        for (Map.Entry<Vertex<Integer>, VertexRole> entry : roles.entrySet()) {
            graphController.highlightRole(entry.getKey(), entry.getValue());
        }
    }

    public void animateSend(SendOperation operation) {
        List<Animation> sendAnimations = getAnimationsPerOperationType(OperationType.SEND, List.of(operation));
        animationRunner.runAnimationsConcurrently(sendAnimations);
    }

    public void animateOpinionChange(ChooseOperation operation) {
        List<Animation> chooseAnimations = getAnimationsPerOperationType(OperationType.CHOOSE, List.of(operation));
        animationRunner.runAnimationsConcurrently(chooseAnimations);
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
                        .map(chooseOperation -> getChooseOpinionAnimation((SmartGraphVertexNode<Integer>) graphController.getGraphView().getStylableVertex(chooseOperation.getVertex().element())))
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

    private Animation getChooseOpinionAnimation(SmartGraphVertexNode<Integer> vertex) {
        return chooseAnimationFactory.getChooseOpinionAnimation(vertex, e -> graphController.changeVertexFillStyle(vertex.getUnderlyingVertex()));
    }

    public void setAnimationsSpeed(Double multiplier){
        sendAnimationFactory.setDuration(sendAnimationFactory.getBaseDuration().divide(multiplier));
        chooseAnimationFactory.setDuration(chooseAnimationFactory.getBaseDuration().divide(multiplier));
    }
}
