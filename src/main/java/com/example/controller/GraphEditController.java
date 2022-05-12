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

    private DrawMenuController toolsController;

    @FXML
    public void initialize(){
        vertexButton.setOnAction(e -> toolsController.selectMode(DrawMode.VERTEX));
        edgeButton.setOnAction(e -> toolsController.selectMode(DrawMode.EDGE));
    }

    public void setToolsController(DrawMenuController controller){
        toolsController = controller;
    }
}
