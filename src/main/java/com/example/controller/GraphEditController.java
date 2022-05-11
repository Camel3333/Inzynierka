package com.example.controller;

import com.example.draw.CreationHelper;
import com.example.draw.DrawMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/graphEditView.fxml")
public class GraphEditController {
    @FXML
    Button vertexButton;
    @FXML
    Button edgeButton;

    private DrawMode selectedMode = DrawMode.NONE;

    @Setter
    private CreationHelper helper;

    @FXML
    public void initialize(){
        vertexButton.setOnAction(e -> selectMode(DrawMode.VERTEX));
        edgeButton.setOnAction(e -> selectMode(DrawMode.EDGE));
    }

    private void selectMode(DrawMode mode){
        selectedMode = mode;
        System.out.println("Selected mode: "+mode);
    }
}
