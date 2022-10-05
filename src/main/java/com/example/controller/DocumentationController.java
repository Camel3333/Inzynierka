package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@FxmlView("/view/documentationView.fxml")
public class DocumentationController {

    @FXML
    ListView<Text> articleList;

    @FXML
    Pane page;


    @FXML
    public void initialize() {
        articleList.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, number, t1) -> {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    Node scene;
                    int index = observableValue.getValue().intValue();
                    String location = "/view/documentationPages/helpQVoter.fxml";
                    if (index == 1) {
                        location = "/view/documentationPages/helpLamport.fxml";
                    }
                    fxmlLoader.setLocation(getClass().getResource(location));
                    try {
                        scene = fxmlLoader.load();
                        page.getChildren().clear();
                        page.getChildren().add(scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
