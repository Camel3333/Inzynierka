package com.example.animation;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.ChooseOperation;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.operations.OperationType;
import com.example.algorithm.operations.SendOperation;
import com.example.algorithm.report.StepReport;
import com.example.controller.GraphController;
import com.example.model.MyVertex;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public abstract class AnimationEngine{
    protected GraphController graphController;

    private int duration;

    public void animate(StepReport report){
        highlightRoles(report.getRoles());
        Map<OperationType, List<Operation>> operationsPerType = report.getOperations().stream().collect(Collectors.groupingBy(Operation::getType));
        operationsPerType.entrySet().stream().forEach(entry -> {
            try {
                animate(entry.getKey(), entry.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    protected abstract void highlightRoles(Map<Vertex<Integer>, VertexRole> roles);

    public void animateSend(SendOperation operation){
        // unpack send operation and animate
        System.out.println("Animating send between "+operation.getFromId()+" and "+operation.getToId()+" message: "+operation.getSentOpinion().getValue());
    }

    public void animateOpinionChange(ChooseOperation operation){
        // unpack opinion change operation and animate
        System.out.println("Animating opinion change for "+operation.getId()+" to "+operation.getChosenOpinion().getValue());
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }

    public void animate(OperationType type, List<Operation> operations) throws InterruptedException {
            switch (type){
                case SEND -> {
                    ArrayList<Semaphore> semaphores = new ArrayList<>();
                    ArrayList<PathTransition> pathTransitions = new ArrayList<>();
                    ArrayList<ImageView> balls = new ArrayList<>();

                    operations.forEach(operation -> {
                        ImageView ball = new ImageView(new Image("file:src/main/resources/ms.jpg", 20, 20, false, false));
                        balls.add(ball);
                        pathTransitions.add(getPathTransition(((SendOperation) operation).getFromId(), ((SendOperation)operation).getToId(), ball));
                        semaphores.add(new Semaphore(0));
                    });

                    for(PathTransition pathTransition : pathTransitions){
                        int index = pathTransitions.indexOf(pathTransition);
                        pathTransition.setOnFinished(e -> Platform.runLater(() -> {
                            ((Pane)(graphController.getGraphRoot().getChildren().stream().toList().get(0))).getChildren().remove(balls.get(index));
                            semaphores.get(index).release();
                        }));
                       pathTransition.play();
                    }

                    for(Semaphore semaphore : semaphores){
                        semaphore.acquire();
                    }
                }
                case CHOOSE -> {
                    List<Thread> animations = new ArrayList<>();
                    operations.forEach(operation -> animateOpinionChange((ChooseOperation) operation));
                    for (Thread animation : animations) {
                        try {
                            animation.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try{
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    private PathTransition getPathTransition(int v1, int v2, ImageView ball){
        MyVertex<Integer> commander1 = (MyVertex<Integer>) graphController.getGraph().vertices().stream().toList().get(v1);
        MyVertex<Integer> commander2 = (MyVertex<Integer>) graphController.getGraph().vertices().stream().toList().get(v2);

        double commander1PositionX = graphController.getGraphView().getVertexPositionX(commander1);
        double commander1PositionY = graphController.getGraphView().getVertexPositionY(commander1);
        double commander2PositionX = graphController.getGraphView().getVertexPositionX(commander2);
        double commander2PositionY = graphController.getGraphView().getVertexPositionY(commander2);

        ball.setX(commander1PositionX);
        ball.setY(commander1PositionY);

        Platform.runLater(() -> ((Pane) (graphController.getGraphRoot().getChildren().stream().toList().get(0))).getChildren().add(ball));

        Path path = new Path();
        path.getElements().add(new MoveTo(commander1PositionX,commander1PositionY));
        path.getElements().add(new LineTo(commander2PositionX, commander2PositionY));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(duration));
        pathTransition.setNode(ball);
        pathTransition.setPath(path);

        return pathTransition;
    }

}
