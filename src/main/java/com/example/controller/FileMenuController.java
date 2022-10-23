package com.example.controller;

import com.example.ApplicationState;
import com.example.model.MyGraph;
import com.example.util.GraphConverter;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Component
@FxmlView("/view/fileMenuView.fxml")
public class FileMenuController {
    @Autowired
    GraphController graphController;
    @Autowired
    AppController appController;
    @FXML
    MenuItem newFileButton;
    @FXML
    MenuItem importButton;
    @FXML
    MenuItem exportButton;

    private void bindButtons() {
        ObjectProperty<ApplicationState> applicationState = appController.getApplicationStateProperty();
        BooleanBinding isSimulation = applicationState.isEqualTo(ApplicationState.SIMULATING);

        newFileButton.disableProperty().bind(isSimulation);

        newFileButton.setOnAction(e -> {
            graphController.setModelGraph(new MyGraph<>());
        });
        exportButton.setOnAction(e -> {
            try {
                exportGraph();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        importButton.setOnAction(e -> {
            try {
                importGraph();
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize() {
        bindButtons();
    }

    private void exportGraph() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showSaveDialog(this.exportButton.getParentPopup().getScene().getWindow());
        if (file != null) {
            GraphConverter.saveGraphML(file, this.graphController.getGraph());
        }
    }

    private void importGraph() throws ParserConfigurationException, IOException, SAXException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph File");
        File graphFile = fileChooser.showOpenDialog(this.importButton.getParentPopup().getScene().getWindow());
        if (graphFile != null) {
            graphController.setModelGraph(GraphConverter.fromML(graphFile));
        }
    }
}
