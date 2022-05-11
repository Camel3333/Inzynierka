package com.example.controller;

import javafx.fxml.FXML;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/mainMenuView.fxml")
public class MainMenuController {
    @FXML
    GraphEditController graphToolsController;
}
