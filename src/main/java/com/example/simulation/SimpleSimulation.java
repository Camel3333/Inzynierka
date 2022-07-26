package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.report.StepReport;
import com.example.animation.AnimationEngine;
import com.example.animation.AnimationEngineFactory;
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
    private AnimationEngine animationEngine;
    private BooleanProperty allowAnimations = new SimpleBooleanProperty(true);
    private AnimationEngineFactory animationEngineFactory = new AnimationEngineFactory();

    @Setter
    private GraphController graphController;

    public SimpleSimulation(GraphController graphController) {
        this.graphController = graphController;
    }

    public void setEnvironment(Algorithm algorithm, AlgorithmSettings settings){
        this.algorithm = algorithm;
        this.settings = settings;
        this.animationEngine = animationEngineFactory.create(algorithm.getType());
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
        graphController.update();
        return report;
    }
}
