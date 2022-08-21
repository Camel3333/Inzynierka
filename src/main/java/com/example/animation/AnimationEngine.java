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
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import lombok.Setter;
import org.springframework.stereotype.Service;

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

        operationsPerType.forEach(this::animateConcurrently);
    }

    protected abstract void highlightRoles(Map<Vertex<Integer>, VertexRole> roles);

    private Map<OperationType, List<Operation>> groupOperationsByType(List<Operation> operations) {
        return operations.stream().collect(Collectors.groupingBy(Operation::getType));
    }

    public void animateSend(SendOperation operation) {
        Point2D fromPosition = graphController.getVertexPosition(operation.getFrom());
        Point2D toPosition = graphController.getVertexPosition(operation.getTo());
        runAnimation(getSendAnimation(fromPosition, toPosition, operation.getSentOpinion().get()));
    }

    public void animateOpinionChange(ChooseOperation operation) {
        SmartGraphVertexNode<Integer> vertexNode = (SmartGraphVertexNode<Integer>) graphController.getGraphView().getStylableVertex(operation.getVertex().element());
        runAnimation(getChooseOpinionAnimation(vertexNode, operation.getChosenOpinion().get()));
    }

    public void animateConcurrently(OperationType type, List<Operation> operations) {
        switch (type) {
            case SEND -> {
                ParallelTransition parallelTransition = new ParallelTransition();

                parallelTransition.getChildren().addAll(operations
                        .stream()
                        .map(operation -> (SendOperation) operation)
                        .map(sendOperation -> {
                            Point2D fromPosition = graphController.getVertexPosition(sendOperation.getFrom());
                            Point2D toPosition = graphController.getVertexPosition(sendOperation.getTo());
                            return getSendAnimation(fromPosition, toPosition, sendOperation.getSentOpinion().get());
                        })
                        .toList());

                runAnimation(parallelTransition);
            }
            case CHOOSE -> {
                ParallelTransition parallelTransition = new ParallelTransition();

                parallelTransition.getChildren().addAll(operations
                        .stream()
                        .map(operation -> (ChooseOperation) operation)
                        .map(chooseOperation -> getChooseOpinionAnimation((SmartGraphVertexNode<Integer>) graphController.getGraphView().getStylableVertex(chooseOperation.getVertex().element()), chooseOperation.getChosenOpinion().get()))
                        .toList());

                runAnimation(parallelTransition);
            }
        }
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

    private PathTransition getSendAnimation(Point2D from, Point2D to, boolean attack) {

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
}
