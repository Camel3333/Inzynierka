package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.algorithm.VertexRole;
import com.example.algorithm.report.StepReport;
import com.example.animation.AnimationEngine;
import com.example.controller.GraphController;
import com.example.information.InformationEngine;
import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Setter;

public class SimpleSimulation implements Simulation {

    private Algorithm algorithm;
    private AlgorithmSettings settings;
    private final AnimationEngine animationEngine;
    @Setter
    private InformationEngine informationEngine;
    private final BooleanProperty allowAnimations = new SimpleBooleanProperty(true);

    @Setter
    private GraphController graphController;

    public SimpleSimulation(GraphController graphController) {
        this.graphController = graphController;
        this.animationEngine = new AnimationEngine(graphController);
    }

    public void setEnvironment(Algorithm algorithm, AlgorithmSettings settings) {
        this.algorithm = algorithm;
        this.settings = settings;
        this.animationEngine.setGraphController(graphController);
    }

    @Override
    public void allowAnimations(boolean allow) {
        allowAnimations.setValue(allow);
    }

    public void loadEnvironment() {
        algorithm.loadEnvironment(graphController.getGraph(), settings);
    }

    public BooleanProperty getIsFinishedProperty() {
        return algorithm.getIsFinishedProperty();
    }

    public StepReport step() {
        StepReport report = algorithm.step();
        if (allowAnimations.get()) {
            graphController.enableGraphInteractions(false);
            if (informationEngine != null)
                informationEngine.processReport(report);
            animationEngine.animate(report);
            graphController.enableGraphInteractions(true);
        }
        if (algorithm.isFinished()) {
            removeSimulationRelatedColoring();
        }
        graphController.update();
        return report;
    }

    @Override
    public void stop() {
        removeSimulationRelatedColoring();
    }

    @Override
    public void setAnimationsSpeed(double speedMultiplier) {
        System.out.println("Changing animation speed to " + speedMultiplier);
        animationEngine.setAnimationsSpeed(speedMultiplier);
    }

    private void removeSimulationRelatedColoring() {
        graphController.getGraph()
                .vertices()
                .forEach(v -> graphController.highlightRole(v, VertexRole.NONE));
    }

    public boolean isGraphEmpty() {
        return graphController.getGraph().vertices().isEmpty();
    }
}
