package com.example.controller;

import com.example.ApplicationState;
import com.example.draw.CreationHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


@Component
@FxmlView("/view/mainMenuView.fxml")
public class MainMenuController implements ChangeListener<ApplicationState> {
    @Autowired
    GraphController graphController;
    @FXML
    MenuItem importButton;
    @FXML
    MenuItem exportButton;
    // Toolbars
    @FXML
    GraphEditController graphToolsController;

    // Draw Menu
    @FXML
    DrawMenuController drawMenuController;

    // Simulation Menu
    @FXML
    SimulationMenuController simulationMenuController;

    private void bindToolbars(){
        graphToolsController.setDrawMenuController(drawMenuController);
        graphToolsController.setSimulationMenuController(simulationMenuController);
        exportButton.setOnAction(e -> {
            try {
                graphController.getGraphML();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        importButton.setOnAction(e -> {
            try {
                graphController.fromML();
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize(){
        bindToolbars();

    }

    public void setDrawingHelper(CreationHelper drawingHelper) {
        drawMenuController.setDrawHelper(drawingHelper);
    }

    @Override
    public void changed(ObservableValue<? extends ApplicationState> observable, ApplicationState oldValue, ApplicationState newValue) {
        switch (newValue){
            case SIMULATING -> {
                drawMenuController.setEnabled(false);
                graphToolsController.setEnabled(false, ApplicationState.DRAWING);

                simulationMenuController.setEnabled(true);
                graphToolsController.setEnabled(true, ApplicationState.SIMULATING);

                simulationMenuController.setChaneStateToSimulationEnabled(false);
                graphToolsController.setChaneStateToSimulationEnabled(false);
            }
            case DRAWING -> {
                drawMenuController.setEnabled(true);
                graphToolsController.setEnabled(true, ApplicationState.DRAWING);

                simulationMenuController.setEnabled(false);
                graphToolsController.setEnabled(false, ApplicationState.SIMULATING);

                simulationMenuController.setChaneStateToSimulationEnabled(true);
                graphToolsController.setChaneStateToSimulationEnabled(true);
            }
        }
    }
}
