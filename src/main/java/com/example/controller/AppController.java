package com.example.controller;


import com.brunomnsilva.smartgraph.graph.Graph;
import com.example.draw.CreationHelper;
import com.example.model.MyGraph;
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

    @FXML
    private MainMenuController menuController;

    private CreationHelper drawingHelper = new CreationHelper();
    private Graph<Integer, Integer> graph = new MyGraph<>();

    public BorderPane getRoot() {
        return root;
    }

    public void initGraph() {
        graphController.setModelGraph(graph);
    }

    public void initGraph1(){
        graphController.initGraph();
    }

}
