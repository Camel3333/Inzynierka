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

    @FXML
    private MenuItem simulateItem;

    private final AppController appController;

    @Autowired
    public SimulationMenuController(AppController appController) {
        this.appController = appController;
    }

    public void changeApplicationState() {
        switch (appController.getApplicationState()){
            case DRAWING -> changeApplicationState(ApplicationState.SIMULATING);
            case SIMULATING -> changeApplicationState(ApplicationState.DRAWING);
        }
    }

    public void changeApplicationState(ApplicationState applicationState) {
        appController.setApplicationState(applicationState);
    }

    public void setEnabled(boolean enabled) {}
}
