package com.example.controller;

import com.example.draw.TraitorsGenerator;
import com.example.model.MyGraph;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/generateTraitorsView.fxml")
public class GenerateTraitorsController {
    @FXML
    private Slider distributionSlider;

    @Autowired
    private TraitorsGenerator traitorsGenerator;

    public void generateTraitors(MyGraph<Integer, Integer> graph) {
        traitorsGenerator.generateTraitors(graph, distributionSlider.getValue()/100);
    }
}
