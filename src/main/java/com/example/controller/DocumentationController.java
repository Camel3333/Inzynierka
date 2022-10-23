package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@FxmlView("/view/documentationView.fxml")
public class DocumentationController {

    private Stage documentationStage = null;

    @FXML
    ListView<Text> articleList;

    @FXML
    Pane page;

    private void setScene(int index, Pane page) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Node scene;


        String location;
        switch (index) {
            case 0:
                return;
            case 1:
                location = "/view/documentationPages/helpLamport.fxml";
                break;
            case 2:
                location = "/view/documentationPages/helpKing.fxml";
                break;
            case 3:
                location = "/view/documentationPages/helpQVoter.fxml";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + index);
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
    @FXML
    public void initialize() {
        articleList.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, number, t1) -> setScene(observableValue.getValue().intValue(), page)
        );
    }

    public void openDocumentation(int index) throws IOException {
        if (documentationStage == null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/documentationView.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Documentation");
            stage.setScene(scene);

            stage.show();
            documentationStage = stage;
        }
        else {
            if (!documentationStage.isShowing()) documentationStage.show();
            if (documentationStage.isIconified()) documentationStage.setIconified(false);
            documentationStage.toFront();
        }

        ((ListView)documentationStage.getScene().lookup("#articleList")).getSelectionModel().select(index);
    }
}
