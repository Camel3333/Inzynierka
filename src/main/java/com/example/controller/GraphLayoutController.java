package com.example.controller;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.example.util.ContentZoomAndMoveHelper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import org.springframework.stereotype.Component;

@Component
public class GraphLayoutController {
    @FXML
    private CheckBox automaticLayout;
    @FXML
    private Slider zoom;

    private ContentZoomAndMoveHelper contentZoomAndMoveHelper;

    public void setGraph(SmartGraphPanel<?,?> graphView) {
        resetAutomaticLayoutCheckbox();
        resetZoomSlider();
        bindAutomaticLayoutCheckbox(graphView);
        contentZoomAndMoveHelper = new ContentZoomAndMoveHelper(graphView);
        bindZoomSlider(contentZoomAndMoveHelper);
    }

    private void resetAutomaticLayoutCheckbox() {
        automaticLayout.selectedProperty().unbind();
        automaticLayout.selectedProperty().setValue(false);
    }

    private void resetZoomSlider() {
        zoom.valueProperty().unbind();
        zoom.setValue(1.0);
    }

    private void bindAutomaticLayoutCheckbox(SmartGraphPanel<?,?> graphView) {
        automaticLayout.selectedProperty().bindBidirectional(graphView.automaticLayoutProperty());
    }

    private void bindZoomSlider(ContentZoomAndMoveHelper contentZoomAndMoveHelper) {
        zoom.valueProperty().bindBidirectional(contentZoomAndMoveHelper.scaleFactorProperty());
    }
}
