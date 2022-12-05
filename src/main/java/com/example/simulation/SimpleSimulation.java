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
        StepReport report;
        if (allowAnimations.get()) {
            graphController.removeAllVerticesListeners();
            graphController.enableGraphInteractions(false);
            report = algorithm.step();
            if (informationEngine != null)
                informationEngine.processReport(report);
            animationEngine.animate(report);
            graphController.addAllVerticesListeners();
            graphController.enableGraphInteractions(true);
            // update coloring if something didn't change in animations
            graphController.getGraph().vertices().forEach(vertex -> graphController.colorVertex(vertex));
        }
        else {
            report = algorithm.step();
        }
        if (algorithm.isFinished()) {
            removeSimulationRelatedColoring();
        }
        graphController.update();
        return report;
    }

    @Override
    public void setAnimationsSpeed(double speedMultiplier) {
        animationEngine.setAnimationsSpeed(speedMultiplier);
    }

    public void removeSimulationRelatedColoring() {
        graphController.getGraph()
                .vertices()
                .forEach(v -> graphController.highlightRole(v, VertexRole.NONE));
    }

    public boolean isGraphEmpty() {
        return graphController.getGraph().vertices().isEmpty();
    }

    @Override
    public void clearData() {
        graphController.clearVerticesTooltips();
    }
}
