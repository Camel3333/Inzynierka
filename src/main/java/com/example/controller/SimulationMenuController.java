package com.example.controller;

import com.example.ApplicationState;
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
    private MenuItem simulateItem;
    @FXML
    private MenuItem nextStepItem;

    private final AppController appController;

    @Autowired
    public SimulationMenuController(AppController appController) {
        this.appController = appController;
    }


    @FXML
    public void initialize(){
        simulateItem.setOnAction(e -> {
            if(i % 2 == 0) {
                changeApplicationState(ApplicationState.SIMULATING);
            }
            else {
                changeApplicationState(ApplicationState.DRAWING);
            }
            i++;
        });
        nextStepItem.setOnAction(e -> {
            doNextStep();
        });
    }

    public void changeApplicationState(ApplicationState applicationState) {
        appController.setApplicationState(applicationState);
    }

    public void doNextStep() {
        appController.getSimulationController().doStep();
    }

    public void setEnabled(boolean enabled) {
//        simulateItem.setDisable(!enabled);
        nextStepItem.setDisable(!enabled);
    }

    public void setChaneStateToSimulationEnabled(boolean enabled) {
        simulateItem.setDisable(!enabled);
    }
}
