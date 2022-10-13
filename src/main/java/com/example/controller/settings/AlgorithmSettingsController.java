package com.example.controller.settings;

import com.example.settings.AlgorithmSettings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;

import java.util.List;

public interface AlgorithmSettingsController {
    BooleanProperty getAreSettingsValidProperty();

    AlgorithmSettings getAlgorithmSettings();

    List<Node> getAllNodes();

    Node getParent();
}
