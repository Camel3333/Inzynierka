package com.example.controller;

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
        addItem("test1");
        addItem("test2");
    }

    public void addItem(String item) {
        listView.getItems().add(item);
    }

    public void showAll() {

    }


}
