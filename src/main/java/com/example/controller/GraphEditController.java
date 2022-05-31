package com.example.controller;

import com.example.draw.DrawMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/graphEditView.fxml")
public class GraphEditController {
    @FXML
    Button vertexButton;
    @FXML
    Button edgeButton;
    @FXML
    Button deleteButton;
    @FXML
    Button noneButton;

    private DrawMenuController toolsController;

    @FXML
    public void initialize(){
        vertexButton.setOnAction(e -> toolsController.selectMode(DrawMode.VERTEX));
        edgeButton.setOnAction(e -> toolsController.selectMode(DrawMode.EDGE));
        deleteButton.setOnAction(e -> toolsController.selectMode(DrawMode.DELETE));
        noneButton.setOnAction(e -> toolsController.selectMode(DrawMode.NONE));
    }

    public void setToolsController(DrawMenuController controller){
        toolsController = controller;
    }
}
