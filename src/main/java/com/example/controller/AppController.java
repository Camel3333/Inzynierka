package com.example.controller;


import com.example.ApplicationState;
import com.example.algorithm.AlgorithmType;
import com.example.draw.CreationHelper;
import com.example.model.MyGraph;
import com.example.simulation.SimpleSimulation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/appView.fxml")
public class AppController {

    @FXML
    private BorderPane root;

    @FXML
    private GraphController graphController;

    @FXML
    private MainMenuController menuController;

    @FXML
    private SimulationController simulationController;

    @FXML
    private StatisticsController statisticsController;

    @Setter
    private ObjectProperty<ApplicationState> applicationStateProperty =
            new SimpleObjectProperty<>();

    private CreationHelper drawingHelper = new CreationHelper();

    private MyGraph<Integer, Integer> graph = new MyGraph<>();

    public BorderPane getRoot() {
        return root;
    }

    public void initGraph() {
        graphController.setModelGraph(graph);
//        initDrawingHelper();
        applicationStateProperty.addListener(menuController);
        setApplicationState(ApplicationState.DRAWING);
    }

    public void initDrawingHelper() {
        graphController.removeObserver(drawingHelper);
        drawingHelper = new CreationHelper();
        drawingHelper.setGraphController(graphController);
        graphController.addObserver(drawingHelper);
        menuController.setDrawingHelper(drawingHelper);
    }

    public void initSimulationController() {
        simulationController.setAvailableAlgorithms(FXCollections.observableArrayList(AlgorithmType.LAMPORT, AlgorithmType.KING, AlgorithmType.QVOTER));
        simulationController.setSimulation(new SimpleSimulation(graphController));
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationStateProperty.set(applicationState);
        switch (applicationState) {
            case DRAWING -> {
                initDrawingHelper();
                simulationController.hide();
            }
            case SIMULATING -> {
                initSimulationController();
                graphController.removeObserver(drawingHelper);
                simulationController.show();
            }
        }
    }

    public ApplicationState getApplicationState(){
        return applicationStateProperty.get();
    }

    public SimulationController getSimulationController() {
        return simulationController;
    }
}
