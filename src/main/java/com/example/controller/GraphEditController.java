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
    Button vertexButton;
    @FXML
    Button edgeButton;
    @FXML
    Button deleteButton;
    @FXML
    Button noneButton;
    @FXML
    Button startButton;


    private DrawMenuController drawMenuController;
    private SimulationMenuController simulationMenuController;

    private void initializeDrawingButtons() {
        vertexButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.VERTEX));
        edgeButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.EDGE));
        deleteButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.DELETE));
        noneButton.setOnAction(e -> drawMenuController.selectMode(DrawMode.NONE));
        buttons.put(ApplicationState.DRAWING,
                new ArrayList<>(List.of(vertexButton, edgeButton, deleteButton, noneButton)));
    }

    private void initializeSimulationButtons() {
        startButton.setOnAction(e -> simulationMenuController.changeApplicationState(ApplicationState.SIMULATING));
        buttons.put(ApplicationState.SIMULATING,
                new ArrayList<>(List.of(vertexButton, edgeButton, deleteButton, noneButton)));
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

    public void setEnabled(boolean enabled, ApplicationState applicationState) {
        buttons.get(applicationState).forEach(button -> {
            button.setManaged(enabled);
            button.setVisible(enabled);
        });
    }
}
