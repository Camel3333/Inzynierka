package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FxmlView("/view/statisticsView.fxml")
public class StatisticsController {
    @FXML
    private LineChart<Integer, Double> opinionChart;

    @FXML
    public void initialize() {
        opinionChart.getYAxis().setLabel("Procent przekonanych za");
        opinionChart.getXAxis().setLabel("Runda");
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>(1, 0.2));
        series.getData().add(new XYChart.Data<>(2, 0.55));
        series.getData().add(new XYChart.Data<>(3, 0.4));
        series.getData().add(new XYChart.Data<>(4, 0.33));
        series.getData().add(new XYChart.Data<>(5, 1.0));

        opinionChart.getData().add(series);

    }
}
