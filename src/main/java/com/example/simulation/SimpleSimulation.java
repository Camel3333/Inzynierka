package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.settings.AlgorithmSettings;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import lombok.Setter;

public class SimpleSimulation extends Service<Boolean> implements Simulation{

    /*
    STEP -> zwraca klasę reprezentująca zmiany
    Po wykonaniu kroku wizualizujemy zmiany - tylko jeśli aktulany tryb symulacji tego wymaga
     */

    private Algorithm algorithm;
    private AlgorithmSettings settings;

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
    }

    @Override
    public void startSimulation() {
        algorithm.loadEnvironment((MyGraph<Integer, Integer>) graphController.getGraph(), settings);
        while (!algorithm.isFinished()){
            algorithm.step();
            try {
                // simulate animation
                Thread.sleep(100);
                System.out.println("Animate!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
}
