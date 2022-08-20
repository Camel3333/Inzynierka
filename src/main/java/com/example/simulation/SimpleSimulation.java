package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.report.StepReport;
import com.example.animation.AnimationEngine;
import com.example.settings.AlgorithmSettings;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Setter;


public class SimpleSimulation implements Simulation{

    /*
    STEP -> zwraca klasę reprezentująca zmiany
    Po wykonaniu kroku wizualizujemy zmiany - tylko jeśli aktulany tryb symulacji tego wymaga
     */

    private Algorithm algorithm;
    private AlgorithmSettings settings;
    private final AnimationEngine animationEngine;
    private final BooleanProperty allowAnimations = new SimpleBooleanProperty(true);

    @Setter
    private GraphController graphController;

    public SimpleSimulation(GraphController graphController) {
        this.graphController = graphController;
        this.animationEngine = new AnimationEngine(graphController);
    }

    public void setEnvironment(Algorithm algorithm, AlgorithmSettings settings){
        this.algorithm = algorithm;
        this.settings = settings;
        this.animationEngine.setGraphController(graphController);
    }

    @Override
    public void allowAnimations(boolean allow) {
        allowAnimations.setValue(allow);
    }

    public void loadEnvironment() {
        algorithm.loadEnvironment((MyGraph<Integer, Integer>) graphController.getGraph(), settings);
    }

    public BooleanProperty getIsFinishedProperty() {
        return algorithm.getIsFinishedProperty();
    }

    public StepReport step() {
        StepReport report = algorithm.step();
        if (allowAnimations.get()){
            animationEngine.animate(report);
        }
        algorithm.checkIsFinished();
        graphController.update();
        return report;
    }
}
