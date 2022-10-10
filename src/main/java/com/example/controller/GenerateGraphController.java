package com.example.controller;

import com.example.draw.DefinedGraph;
import com.example.draw.GraphGenerator;
import com.example.settings.AlgorithmSetting;
import com.example.settings.IntegerSettingTextField;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/generateGraphView.fxml")
public class GenerateGraphController {
    @FXML
    private ComboBox<DefinedGraph> graphBox;

    @FXML
    private Slider verticesSlider;

    @FXML
    private IntegerSettingTextField vertices;

    @FXML
    private ButtonType ok;

    @Autowired
    private GraphGenerator graphGenerator;

    @Setter
    private DefinedGraph selectedDefinedGraph;

    public GenerateGraphController() {
    }

    public void generateGraph(GraphController graphController) {
        if (vertices.getIsValidProperty().get()) {
            graphGenerator.generateGraph(graphController, selectedDefinedGraph,
                    vertices.getContainedSetting().get().getValue());
        }
    }

    @FXML
    public void initialize() {
        graphBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DefinedGraph definedGraph, boolean empty) {
                super.updateItem(definedGraph, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(definedGraph.toString());
                }
            }
        });

        graphBox.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setSelectedDefinedGraph(newValue);
                    }
                }));

        ObservableList<DefinedGraph> definedGraphs = FXCollections.observableArrayList(DefinedGraph.FULL);
        graphBox.setItems(definedGraphs);
        graphBox.getSelectionModel().select(0);

        vertices.setContainedSetting(new AlgorithmSetting<>
                ("vertices", (int) verticesSlider.getValue(), Integer.class,
                        (value) -> value >= verticesSlider.getMin() && value <= verticesSlider.getMax()));

        verticesSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                vertices.getContainedSetting().get().setValue(newValue.intValue());
            }
        });
        vertices.getContainedSetting().get().getValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                verticesSlider.setValue(newValue);
            }
        });
    }
}
