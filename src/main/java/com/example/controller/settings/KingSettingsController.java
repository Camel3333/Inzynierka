package com.example.controller.settings;

import com.example.model.MyGraph;
import com.example.settings.*;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FxmlView("/view/settings/kingSettingsView.fxml")
public class KingSettingsController implements AlgorithmSettingsController {
    @Getter
    @FXML
    public Node parent;
    @FXML
    public Label phaseLabel;
    @FXML
    private IntegerSettingTextField phase;
    private SettingNodesGroup settingNodesGroup;
    @Getter
    private AlgorithmSettings algorithmSettings = new AlgorithmSettings();

    @FXML
    public void initialize() {
        setDefaultSettings();
        settingNodesGroup = new SettingNodesGroupImpl(Collections.singletonList(phase));
    }

    @Override
    public BooleanProperty getAreSettingsValidProperty() {
        return settingNodesGroup.getAreAllValidProperty();
    }

    @Override
    public List<Node> getAllNodes() {
        return settingNodesGroup.getAllNodes().stream().map(settingNode -> (Node) settingNode).collect(Collectors.toList());
    }

    @Override
    public void adjustSettingsConditions(MyGraph<Integer, Integer> graph) {
        AlgorithmSetting<Integer> phaseSetting = (AlgorithmSetting<Integer>) algorithmSettings.getSettings().get("phase");
        phaseSetting.setValue(graph.numVertices());
    }

    private void setDefaultSettings() {
        AlgorithmSetting<Integer> phaseSetting = new AlgorithmSetting<>("phase", 1, Integer.class, (value) -> value > 0);
        algorithmSettings.getSettings().put("phase", phaseSetting);
        phase.setContainedSetting(phaseSetting);
    }
}
