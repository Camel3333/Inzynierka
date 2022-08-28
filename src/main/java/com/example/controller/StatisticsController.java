package com.example.controller;

import com.sun.glass.ui.Clipboard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@FxmlView("/view/statisticsView.fxml")
public class StatisticsController {
    @FXML
    private LineChart<Integer, Integer> opinionChart;

    private int nextX = 1;
    private XYChart.Series<Integer, Integer> supporting = new XYChart.Series<>();
    private XYChart.Series<Integer, Integer> notSupporting = new XYChart.Series<>();

    @FXML
    public void initialize() {
        opinionChart.getYAxis().setLabel("Procent przekonanych za");
        opinionChart.getXAxis().setLabel("Runda");
        opinionChart.getYAxis().setAutoRanging(true); //todo numVertices as max range
        supporting.setName("For attack");
        notSupporting.setName("For defense");
        opinionChart.getData().add(supporting);
        opinionChart.getData().add(notSupporting);
    }

    public void addStats(int numSupporting, int numNotSupporting) {
        Platform.runLater(
                () -> {
                    supporting.getData().add(new XYChart.Data<>(nextX, numSupporting));
                    notSupporting.getData().add(new XYChart.Data<>(nextX, numNotSupporting));
                    nextX++;
                }
        );

    }
}
