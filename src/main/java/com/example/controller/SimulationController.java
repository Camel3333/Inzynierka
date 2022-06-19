package com.example.controller;

import com.example.ApplicationState;
import com.example.algorithm.Algorithm;
import com.example.algorithm.AlgorithmSetting;
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

    private AlgorithmSettings algorithmSettings = new AlgorithmSettings();

    private final Map<AlgorithmType, List<Node>> options = new HashMap<>();

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

    private void setDefaultSettings() {
        algorithmSettings.getSettings().put((String) depth.getUserData(),
                new AlgorithmSetting<>((String) depth.getUserData(), 1, Integer.class, (value) -> value >= 0));
        algorithmSettings.getSettings().put((String) phase.getUserData(),
                new AlgorithmSetting<>((String) phase.getUserData(), 1, Integer.class, (value) -> value >= 0));
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
                if (empty) {
                    setText(null);
                } else {
                    setText(algorithmType.toString());
                }
            }
        });

        algorithmsBox.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        showAlgorithmSettings(newValue);
                    }
                }));

        depth.textProperty().addListener((observable, oldValue, newValue) -> {
            int intValue;
            try {
                intValue = Integer.parseInt(newValue);
            }catch(NumberFormatException e){
                depth.setStyle("-fx-text-box-border: red ;-fx-focus-color: red ;");
                return;
            }
            var setting = algorithmSettings.getSettings().get((String) depth.getUserData());
            if (setting.isProperValue(intValue)){
                setting.setValue(intValue);
                depth.setStyle("");
            }
            else{
                depth.setStyle("-fx-text-box-border: red ;-fx-focus-color: red ;");
                return;
            }
        });

        phase.textProperty().addListener((observable, oldValue, newValue) -> {
            int intValue;
            try {
                intValue = Integer.parseInt(newValue);
            }catch(NumberFormatException e){
                phase.setStyle("-fx-text-box-border: red ;-fx-focus-color: red ;");
                return;
            }
            var setting = algorithmSettings.getSettings().get((String) phase.getUserData());
            if (setting.isProperValue(intValue)){
                setting.setValue(intValue);
                phase.setStyle("");
            }
            else{
                phase.setStyle("-fx-text-box-border: red ;-fx-focus-color: red ;");
                return;
            }
        });

    }

    private void showAlgorithmSettings(AlgorithmType algorithmType) {
        hideAlgorithmSettings();
        options.get(algorithmType).forEach(node -> {
            node.setVisible(true);
            node.setManaged(true);
            String settingName = (String) node.getUserData();
            if (algorithmSettings.getSettings().containsKey(settingName)) {
                fillSettingView(node);
            }
        });
    }

    private void fillSettingView(Node node){
        String settingName = (String) node.getUserData();
        if (node instanceof TextField) {
            ((TextField) node).setText(algorithmSettings.getSettings().get(settingName).getValue().toString());
        }
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

//    private boolean fillSelectedAlgorithmSettings(AlgorithmType algorithmType) {
//        boolean valueFailed = false;
//        for (Node node : options.get(algorithmType)){
//            String optionName = (String) node.getUserData();
//            if (node instanceof TextField) {
//                String selectedOption = ((TextField) node).getText();
//                if (algorithmSettings.getSettings().get(optionName).isProperValue(selectedOption))
//                    algorithmSettings.getSettings().get(optionName).setValue(selectedOption);
//                else{
//                    valueFailed = true;
//                    System.out.println("Invalid input for "+optionName);
//                }
//            }
//        }
//        return !valueFailed;
//    }
//
//    private boolean verifySettings(AlgorithmType algorithmType){
//        options.get(algorithmType).forEach(node ->{
//            String optionName = (String) node.getUserData();
//            if (node instanceof TextField) {
//                String selectedOption = ((TextField) node).getText();
//                if (algorithmSettings.getSettings().get(optionName).isProperValue(selectedOption))
//                    algorithmSettings.getSettings().get(optionName).setValue(selectedOption);
//                else{
//                    valueFailed = true;
//                    System.out.println("Invalid input for "+optionName);
//                }
//            }
//        });
//    }

    public void startAlgorithm() {
        AlgorithmType selectedAlgorithm = algorithmsBox.getValue();
        simulation.start(selectedAlgorithm.getAlgorithm(), algorithmSettings);
//        if(fillSelectedAlgorithmSettings(selectedAlgorithm))
//            simulation.start(selectedAlgorithm.getAlgorithm(), algorithmSettings);
//        else
//            System.out.println("Can't run algorithm because some options have invalid type");
    }

}
