package com.example.controller;


import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/appView.fxml")
public class AppController {

    @FXML
    private BorderPane root;

    @FXML
    private GraphController graphController;

    public BorderPane getRoot() {
        return root;
    }

    public void initGraph() {
        graphController.initGraph();
    }

    @FXML
    public void initialize(){
    }
}
