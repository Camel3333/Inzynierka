package com.example.controller.settings;

import com.example.algorithm.ProbabilityType;
import com.example.settings.*;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@FxmlView("/view/settings/qVoterSettingsView.fxml")
public class QVoterSettingsController implements AlgorithmSettingsController {
    @Getter
    @FXML
    public Node parent;
    @FXML
    public Label qLabel;
    @FXML
    public Label timeLabel;
    @FXML
    public Label probabilityLabel;
    @FXML
    private IntegerSettingTextField q;
    @FXML
    private IntegerSettingTextField time;
    @FXML
    private SettingComboBox<ProbabilityType> probabilityBox;
    private SettingNodesGroup settingNodesGroup;
    @Getter
    private AlgorithmSettings algorithmSettings = new AlgorithmSettings();

    @FXML
    public void initialize() {
        setDefaultSettings();
        initializeProbabilityBox();
        settingNodesGroup = new SettingNodesGroupImpl(List.of(q, time, probabilityBox));
    }

    @Override
    public BooleanProperty getAreSettingsValidProperty() {
        return settingNodesGroup.getAreAllValidProperty();
    }

    @Override
    public List<Node> getAllNodes() {
        return settingNodesGroup.getAllNodes().stream().map(settingNode -> (Node) settingNode).collect(Collectors.toList());
    }

    private void setDefaultSettings() {
        AlgorithmSetting<Integer> qSetting = new AlgorithmSetting<>("q", 1, Integer.class, (value) -> value >= 0);
        AlgorithmSetting<Integer> timeSetting = new AlgorithmSetting<>("time", 1, Integer.class, (value) -> value >= 0);
        AlgorithmSetting<ProbabilityType> probabilitySetting = new AlgorithmSetting<>("probability", ProbabilityType.LINEAR, ProbabilityType.class, (value) -> true);
        q.setContainedSetting(qSetting);
        time.setContainedSetting(timeSetting);
        probabilityBox.setContainedSetting(probabilitySetting);
        algorithmSettings.getSettings().put("q", qSetting);
        algorithmSettings.getSettings().put("time", timeSetting);
        algorithmSettings.getSettings().put("probability", probabilitySetting);
    }

    private void initializeProbabilityBox() {
        probabilityBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ProbabilityType probabilityType, boolean empty) {
                super.updateItem(probabilityType, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(probabilityType.toString());
                }
            }
        });

        probabilityBox.setItems(FXCollections.observableArrayList(List.of(ProbabilityType.LINEAR, ProbabilityType.BOLTZMANN)));
        probabilityBox.getSelectionModel().select(0);
    }
}
