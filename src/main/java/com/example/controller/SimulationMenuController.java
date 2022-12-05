package com.example.controller;

import com.example.ApplicationState;
import com.example.controller.settings.TraitorSettings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/simulationMenuView.fxml")
public class SimulationMenuController {

    @FXML
    public MenuItem startItem;
    @FXML
    public MenuItem nextStepItem;
    @FXML
    public MenuItem liveItem;
    @FXML
    public MenuItem instantFinishItem;
    @FXML
    public MenuItem pauseItem;
    @FXML
    public MenuItem simulateItem;
    @FXML
    public MenuItem stopItem;
    @FXML
    public MenuItem traitorSettings;

    @Getter
    private final AppController appController;

    @Autowired
    public SimulationMenuController(AppController appController) {
        this.appController = appController;
    }

    public void changeApplicationState() {
        switch (appController.getApplicationState()) {
            case DRAWING -> changeApplicationState(ApplicationState.SIMULATING);
            case SIMULATING -> changeApplicationState(ApplicationState.DRAWING);
        }
    }

    public void changeApplicationState(ApplicationState applicationState) {
        appController.setApplicationState(applicationState);
    }

    @FXML
    public void initialize(){
        bindItems();
        startItem.setOnAction(e -> appController.getSimulationController().initSimulation());
        nextStepItem.setOnAction(e -> appController.getSimulationController().doStep());
        liveItem.setOnAction(e -> appController.getSimulationController().live());
        instantFinishItem.setOnAction(e -> appController.getSimulationController().instantFinish());
        pauseItem.setOnAction(e -> appController.getSimulationController().pause());
        stopItem.setOnAction(e -> appController.getSimulationController().stop());

        traitorSettings.setOnAction(e -> {
            TraitorSettings.setTraitorsAlwaysLie(!TraitorSettings.isTraitorsAlwaysLie());
            traitorSettings.setText(TraitorSettings.isTraitorsAlwaysLie() ? "✓ Traitors always lie" : "✓ Traitors lie randomly");
        });
        traitorSettings.setText(TraitorSettings.isTraitorsAlwaysLie() ? "✓ Traitors always lie" : "✓ Traitors lie randomly");
    }

    public void bindItems() {
        SimulationController simulationController = appController.getSimulationController();
        ObjectProperty<ApplicationState> applicationState = appController.getApplicationStateProperty();
        BooleanBinding isNotSimulation = applicationState.isEqualTo(ApplicationState.SIMULATING).not();

        startItem.disableProperty().bind(simulationController.getStartDisabledProperty().or(isNotSimulation));
        nextStepItem.disableProperty().bind(simulationController.getNextStepDisabledProperty().or(isNotSimulation));
        liveItem.disableProperty().bind(simulationController.getLiveDisabledProperty().or(isNotSimulation));
        instantFinishItem.disableProperty().bind(simulationController.getInstantFinishDisabledProperty().or(isNotSimulation));
        pauseItem.disableProperty().bind(simulationController.getPauseDisabledProperty().or(isNotSimulation));
        stopItem.disableProperty().bind(simulationController.getStopDisableProperty().or(isNotSimulation));

        traitorSettings.disableProperty().bind(simulationController.getStartDisabledProperty());
    }
}
