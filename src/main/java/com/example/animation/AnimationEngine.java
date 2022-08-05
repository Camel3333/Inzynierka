package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.OperationType;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.controller.GraphController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
public abstract class AnimationEngine{
    @Setter
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
        Point2D fromPosition = graphController.getVertexPosition(operation.getFrom());
        Point2D toPosition = graphController.getVertexPosition(operation.getTo());
        runAnimation(getSendAnimation(fromPosition, toPosition));
    }

    public void animateOpinionChange(ChooseOperation operation){
        // unpack opinion change operation and animate
        SmartGraphVertexNode<Integer> vertexNode = (SmartGraphVertexNode<Integer>) graphController.getGraphView().getStylableVertex(operation.getVertex().element());
        runAnimation(getChooseOpinionAnimation(vertexNode, operation.getChosenOpinion().get()));
//        System.out.println("Animating opinion change for "+operation.getId()+" to "+operation.getChosenOpinion().getValue());
    }

    public void animateConcurrently(OperationType type, List<Operation> operations){
        switch (type){
            case SEND -> {
                ParallelTransition parallelTransition = new ParallelTransition();

                parallelTransition.getChildren().addAll(operations
                        .stream()
                        .map(operation -> (SendOperation) operation)
                        .map(sendOperation -> {
                            Point2D fromPosition = graphController.getVertexPosition(sendOperation.getFrom());
                            Point2D toPosition = graphController.getVertexPosition(sendOperation.getTo());
                            return getSendAnimation(fromPosition, toPosition);
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

    private void runAnimation(Animation animation){
        Semaphore semaphore = new Semaphore(0);

        animation.setOnFinished(e -> {
            System.out.println("Animation finished");
            semaphore.release();
        });

        animation.play();

        try {
            // wait until animation will finish
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private PathTransition getSendAnimation(Point2D from, Point2D to){

        ImageView ball = new ImageView(new Image("file:src/main/resources/ms.jpg", 20, 20, false, false));
        ball.setX(from.getX());
        ball.setY(from.getY());

        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> {
            try {
                graphController.addNodeToView(ball);
                semaphore.release();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Path path = new Path();
        path.getElements().add(new MoveTo(from.getX(), from.getY()));
        path.getElements().add(new LineTo(to.getX(), to.getY()));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(sendDuration);
        pathTransition.setNode(ball);
        pathTransition.setPath(path);

        pathTransition.setOnFinished(
                e -> {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            graphController.removeNodeFromView(ball);
                        }
                    });
                });

        return pathTransition;
    }

    private Animation getChooseOpinionAnimation(SmartGraphVertexNode<Integer> vertex, boolean attack){
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(1000), vertex);
        scaleUp.setToX(1.25);
        scaleUp.setToY(1.25);

        String vertexStyle = attack ? "traitor" : "vertex";

        scaleUp.setOnFinished(e -> graphController.setVertexStyle(vertex.getUnderlyingVertex().element(), vertexStyle));

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(1000), vertex);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        return new SequentialTransition(scaleUp, scaleDown);
    }
}
