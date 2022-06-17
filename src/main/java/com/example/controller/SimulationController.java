package com.example.controller;

import com.example.ApplicationState;
import com.example.algorithm.Algorithm;
import com.example.algorithm.AlgorithmSettings;
import com.example.algorithm.AlgorithmType;
import com.example.simulation.Simulation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@FxmlView("/view/simulationOptionsView.fxml")
public class SimulationController {
    @FXML
    public Label depthLabel;
    @FXML
    public Label phaseLabel;
    @FXML
    private VBox parent;
    @FXML
    private TextField depth;
    @FXML
    private TextField phase;
    @FXML
    private Button startButton;
    @FXML
    private ComboBox<AlgorithmType> algorithmsBox;

    private AlgorithmSettings algorithmSettings;

    private final Map<AlgorithmType, List<Node>> options = new HashMap<>();

    private final Map<String, Object> defaultSettings = new HashMap<>();

    private ObservableList<AlgorithmType> availableAlgorithms = FXCollections.emptyObservableList();

    @Setter
    private Simulation simulation;

    public void show() {
        parent.setVisible(true);
        parent.setManaged(true);
    }

    public void hide() {
        parent.setVisible(false);
        parent.setManaged(false);
    }

    private void setDefaultSettings(){
        defaultSettings.put((String)depth.getUserData(), 1);
        defaultSettings.put((String)phase.getUserData(), 1);
    }

    @FXML
    public void initialize() {
        setDefaultSettings();
        options.put(AlgorithmType.LAMPORT, new ArrayList<>(List.of(depth, depthLabel)));
        options.put(AlgorithmType.KING, new ArrayList<>(List.of(phase, phaseLabel)));
        hideAlgorithmSettings();
        algorithmsBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AlgorithmType algorithmType, boolean empty) {
                super.updateItem(algorithmType, empty);
                if(empty) {
                    setText(null);
                }
                else {
                    setText(algorithmType.toString());
                }
            }
        });

        algorithmsBox.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if(newValue != null) {
                        showAlgorithmSettings(newValue);
                    }
                }));
    }

    private void showAlgorithmSettings(AlgorithmType algorithmType) {
        hideAlgorithmSettings();
        algorithmSettings = new AlgorithmSettings();
        options.get(algorithmType).forEach(node -> {
            node.setVisible(true);
            node.setManaged(true);
            String optionName = (String) node.getUserData();
            algorithmSettings.getSettings()
                    .put(optionName, defaultSettings.get(optionName));
            if (node instanceof TextField) {
                ((TextField) node).setText(defaultSettings.get(optionName).toString());
            }
        });
    }

    private void hideAlgorithmSettings() {
        options.values().stream()
                .flatMap(Collection::stream)
                .forEach(node -> {
                    node.setVisible(false);
                    node.setManaged(false);
                });
    }

    public void setAvailableAlgorithms(ObservableList<AlgorithmType> algorithmTypes) {
        availableAlgorithms = algorithmTypes;
        algorithmsBox.setItems(availableAlgorithms);
        algorithmsBox.getSelectionModel().select(0);
    }

    private void fillSelectedAlgorithmSettings(AlgorithmType algorithmType){
        options.get(algorithmType).forEach(node -> {
            String optionName = (String) node.getUserData();
            if (node instanceof TextField) {
                String selectedOption = ((TextField) node).getText();
                int optionValue = Integer.parseInt(selectedOption);
                algorithmSettings.getSettings().put(selectedOption, optionValue);
            }
        });
    }

    public void startAlgorithm() {
        AlgorithmType selectedAlgorithm = algorithmsBox.getValue();
        try{
            fillSelectedAlgorithmSettings(selectedAlgorithm);
            simulation.start(selectedAlgorithm.getAlgorithm(), algorithmSettings);
        } catch (NumberFormatException e){
            System.out.println("Can't run algorithm because some options have invalid type");
        }
    }

}
