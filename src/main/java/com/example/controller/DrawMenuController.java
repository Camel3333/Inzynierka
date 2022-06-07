package com.example.controller;

import com.example.draw.CreationHelper;
import com.example.draw.DrawMode;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/drawMenuView.fxml")
public class DrawMenuController {
    @FXML
    MenuItem vertexItem;
    @FXML
    MenuItem edgeItem;
    @FXML
    MenuItem deleteItem;
    @FXML
    MenuItem noneItem;

    @Setter
    private CreationHelper drawHelper;

    public void selectMode(DrawMode mode){
        drawHelper.setDrawMode(mode);
        System.out.println("Selected mode: "+mode);
    }

    @FXML
    public void initialize(){
        vertexItem.setOnAction(e -> selectMode(DrawMode.VERTEX));
        edgeItem.setOnAction(e -> selectMode(DrawMode.EDGE));
        deleteItem.setOnAction(e -> selectMode(DrawMode.DELETE));
        noneItem.setOnAction(e -> selectMode(DrawMode.NONE));
    }

    public void setEnabled(boolean enabled) {
        vertexItem.setDisable(!enabled);
        edgeItem.setDisable(!enabled);
        deleteItem.setDisable(!enabled);
        noneItem.setDisable(!enabled);
    }
}
