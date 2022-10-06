package com.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/loggerView.fxml")
public class LoggerController {
    @FXML
    public ListView<String> listView;

    @FXML
    public void initialize() {
    }

    public void addItem(String item) {
        Platform.runLater(() -> listView.getItems().add(item));
    }

}
