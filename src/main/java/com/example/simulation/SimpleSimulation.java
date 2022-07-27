package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.report.StepReport;
import com.example.animation.AnimationEngine;
import com.example.animation.AnimationEngineFactory;
import com.example.settings.AlgorithmSettings;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import lombok.Setter;

import java.util.List;

public class SimpleSimulation extends Service<Boolean> implements Simulation{

    /*
    STEP -> zwraca klasę reprezentująca zmiany
    Po wykonaniu kroku wizualizujemy zmiany - tylko jeśli aktulany tryb symulacji tego wymaga
     */

    private Algorithm algorithm;
    private AlgorithmSettings settings;
    private AnimationEngine animationEngine;
    private BooleanProperty allowAnimations = new SimpleBooleanProperty(true);
    private AnimationEngineFactory animationEngineFactory = new AnimationEngineFactory();

    @Setter
    private GraphController graphController;

    public SimpleSimulation(GraphController graphController) {
        this.graphController = graphController;
        setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("Algorithm succeeded: "+event.getSource().getValue());
            }
        });
    }

    public void setEnvironment(Algorithm algorithm, AlgorithmSettings settings){
        this.algorithm = algorithm;
        this.settings = settings;
        this.animationEngine = animationEngineFactory.create(algorithm.getType());
        this.animationEngine.setGraphController(graphController);
    }

    @Override
    public void allowAnimations(boolean allow) {
        allowAnimations.setValue(allow);
    }

    @Override
    public void startSimulation() {
        algorithm.loadEnvironment((MyGraph<Integer, Integer>) graphController.getGraph(), settings);
        while (!algorithm.isFinished()){
            StepReport report = algorithm.step();
            // simulate animation
            if (allowAnimations.get()){
                animationEngine.animate(report);
            }
            // collect statistics
        }
        graphController.update();
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                startSimulation();
                return true;
            }
        };
    }


    public void loadEnvironment() {
        algorithm.loadEnvironment((MyGraph<Integer, Integer>) graphController.getGraph(), settings);
    }

    public boolean isFinished() {
        return algorithm.isFinished();
    }

    public BooleanProperty getIsFinishedProperty() {
        return algorithm.getIsFinishedProperty();
    }

    public StepReport step() {
        StepReport report = algorithm.step();
        if (allowAnimations.get()){
            animationEngine.animate(report);
        }
        graphController.update();
        return report;
    }
}
