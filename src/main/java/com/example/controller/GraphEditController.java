package com.example.controller;

import com.example.ApplicationState;
import com.example.draw.DrawMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@FxmlView("/view/graphEditView.fxml")
public class GraphEditController {

    Map<ApplicationState, List<Button>> buttons = new HashMap<>();

    @FXML
    private Button vertexButton;
    @FXML
    private Button edgeButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button noneButton;
    @FXML
    private Button simulateButton;
    @FXML
    private Button startButton;
    @FXML
    private Button nextStepButton;
    @FXML
    private Button liveButton;
    @FXML
    private Button instantFinishButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button drawButton;


    private DrawMenuController drawMenuController;
    private SimulationMenuController simulationMenuController;
    private SimulationController simulationController;

    private void initializeDrawingButtons() {
        vertexButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.VERTEX));
        edgeButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.EDGE));
        deleteButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.DELETE));
        noneButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.NONE));
        buttons.put(ApplicationState.DRAWING,
                new ArrayList<>(List.of(vertexButton, edgeButton, deleteButton, noneButton)));
    }

    private void initializeSimulationButtons() {
        startButton.setOnAction(e -> simulationMenuController.startItem.fire());
        nextStepButton.setOnAction(e -> simulationMenuController.nextStepItem.fire());
        liveButton.setOnAction(e -> simulationMenuController.liveItem.fire());
        instantFinishButton.setOnAction(e -> simulationMenuController.instantFinishItem.fire());
        pauseButton.setOnAction(e -> simulationMenuController.pauseItem.fire());
        simulateButton.setOnAction(e -> simulationMenuController.changeApplicationState(ApplicationState.SIMULATING));
        drawButton.setOnAction(e -> simulationMenuController.changeApplicationState(ApplicationState.DRAWING));
        buttons.put(ApplicationState.SIMULATING,
                new ArrayList<>(List.of(startButton, nextStepButton, liveButton, instantFinishButton, pauseButton, drawButton)));
    }

    @FXML
    public void initialize(){
        initializeDrawingButtons();
        initializeSimulationButtons();
    }

    public void setDrawMenuController(DrawMenuController controller){
        drawMenuController = controller;
    }

    public void setSimulationMenuController(SimulationMenuController controller){
        simulationMenuController = controller;
    }

    public void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
        bindButtons();
    }

    public void setEnabled(boolean enabled, ApplicationState applicationState) {
        buttons.get(applicationState).forEach(button -> {
            button.setManaged(enabled);
            button.setVisible(enabled);
        });
    }

    public void setChaneStateToSimulationEnabled(boolean enabled) {
        simulateButton.setManaged(enabled);
        simulateButton.setVisible(enabled);
    }

    public void bindButtons() {
        startButton.disableProperty().bind(simulationController.getStartDisabledProperty());
        nextStepButton.disableProperty().bind(simulationController.getNextStepDisabledProperty());
        liveButton.disableProperty().bind(simulationController.getLiveDisabledProperty());
        instantFinishButton.disableProperty().bind(simulationController.getInstantFinishDisabledProperty());
        pauseButton.disableProperty().bind(simulationController.getPauseDisabledProperty());
    }
}
