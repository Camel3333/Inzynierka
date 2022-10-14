package com.example.controller;

import com.example.controller.graphGeneratorSettings.*;
import com.example.draw.DefinedGraph;
import com.example.draw.GraphGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({"fullGraph", "cycleGraph", "treeGraph", "planarGraph", "bipartiteGraph"})
@FxmlView("/view/generateGraphView.fxml")
public class GenerateGraphController {
    @FXML
    private ComboBox<DefinedGraph> graphBox;

    @FXML
    private FullGraphSettingsController fullGraphSettingsController;

    @FXML
    private CycleGraphSettingsController cycleGraphSettingsController;

    @FXML
    private TreeGraphSettingsController treeGraphSettingsController;

    @FXML
    private PlanarGraphSettingsController planarGraphSettingsController;

    @FXML
    private BipartiteGraphSettingsController bipartiteGraphSettingsController;

    @Autowired
    private GraphGenerator graphGenerator;

    @Setter
    private DefinedGraph selectedDefinedGraph;

    @Setter
    private GraphSettings selectedGraphSettings;

    public GenerateGraphController() { }

    public void generateGraph(GraphController graphController) {
        if (selectedGraphSettings.isValid()) {
            graphGenerator.generateGraph(graphController, selectedDefinedGraph,
                    selectedGraphSettings.getSettings());
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
                        setGraphSettings(newValue);
                    }
                }));

        ObservableList<DefinedGraph> definedGraphs = FXCollections.observableArrayList(DefinedGraph.FULL,
                DefinedGraph.CYCLE, DefinedGraph.BIPARTITE, DefinedGraph.TREE, DefinedGraph.PLANAR);
        graphBox.setItems(definedGraphs);
        graphBox.getSelectionModel().select(0);
    }

    private void setGraphSettings(DefinedGraph definedGraph) {
        setSelectedDefinedGraph(definedGraph);

        switch (selectedDefinedGraph) {
            case FULL -> selectedGraphSettings = fullGraphSettingsController;
            case TREE -> selectedGraphSettings = treeGraphSettingsController;
            case CYCLE -> selectedGraphSettings = cycleGraphSettingsController;
            case PLANAR -> selectedGraphSettings = planarGraphSettingsController;
            case BIPARTITE -> selectedGraphSettings = bipartiteGraphSettingsController;
        }

        fullGraphSettingsController.setVisible(definedGraph);
        treeGraphSettingsController.setVisible(definedGraph);
        cycleGraphSettingsController.setVisible(definedGraph);
        planarGraphSettingsController.setVisible(definedGraph);
        bipartiteGraphSettingsController.setVisible(definedGraph);
    }
}
