package com.example.controller;


import com.brunomnsilva.smartgraph.graph.Graph;
import com.example.ApplicationState;
import com.example.draw.CreationHelper;
import com.example.model.MyGraph;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
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

    @Setter
    private ObjectProperty<ApplicationState> applicationStateProperty =
            new SimpleObjectProperty<>(ApplicationState.DRAWING);

    private CreationHelper drawingHelper = new CreationHelper();

    private Graph<Integer, Integer> graph = new MyGraph<>();

    public BorderPane getRoot() {
        return root;
    }

    public void initGraph() {
        initDrawingHelper();
        initMenu();
        graphController.setModelGraph(graph);
        graphController.setDrawingHelper(drawingHelper);
        applicationStateProperty.addListener(menuController);
    }

    public void initDrawingHelper() {
        drawingHelper.setGraphController(graphController);
        graphController.addObserver(drawingHelper);
    }

    public void initMenu() {
        menuController.setDrawingHelper(drawingHelper);
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationStateProperty.set(applicationState);
    }
}
