package com.example.controller;

import com.example.draw.CreationHelper;
import javafx.fxml.FXML;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/mainMenuView.fxml")
public class MainMenuController {
    // Toolbars
    @FXML
    GraphEditController graphToolsController;

    // Main Menu
    @FXML
    DrawMenuController drawMenuController;

    private void bindToolbars(){
        graphToolsController.setToolsController(drawMenuController);
    }

    @FXML
    public void initialize(){
        bindToolbars();
    }

    public void setDrawingHelper(CreationHelper drawingHelper) {
        drawMenuController.setDrawHelper(drawingHelper);
    }
}
