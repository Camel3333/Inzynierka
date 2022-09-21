package com.example.controller;

import com.example.ApplicationState;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/simulationMenuView.fxml")
public class SimulationMenuController {

    private int i = 0;

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
    private MenuItem simulateItem;
    @FXML
    private MenuItem drawItem;

    private final AppController appController;

    @Autowired
    public SimulationMenuController(AppController appController) {
        this.appController = appController;
    }


    @FXML
    public void initialize(){
        bindItems();
        startItem.setOnAction(e -> appController.getSimulationController().initSimulation());
        nextStepItem.setOnAction(e -> appController.getSimulationController().doStep());
        liveItem.setOnAction(e -> appController.getSimulationController().live());
        instantFinishItem.setOnAction(e -> appController.getSimulationController().instantFinish());
        pauseItem.setOnAction(e -> appController.getSimulationController().pause());
        simulateItem.setOnAction(e -> {
            if(i % 2 == 0) {
                changeApplicationState(ApplicationState.SIMULATING);
            }
            else {
                changeApplicationState(ApplicationState.DRAWING);
            }
            i++;
        });
        drawItem.setOnAction(e -> changeApplicationState(ApplicationState.DRAWING));
    }

    public void changeApplicationState(ApplicationState applicationState) {
        appController.setApplicationState(applicationState);
    }

    public void setEnabled(boolean enabled) {
//        simulateItem.setDisable(!enabled);
        drawItem.setDisable(!enabled);
    }

    public void setChaneStateToSimulationEnabled(boolean enabled) {
        simulateItem.setDisable(!enabled);
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
    }
}
