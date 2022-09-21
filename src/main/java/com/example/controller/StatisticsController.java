package com.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/statisticsView.fxml")
public class StatisticsController {
    @FXML
    private LineChart<Number, Number> opinionChart;

    private int nextX = 1;
    private final XYChart.Series<Number, Number> supporting = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> notSupporting = new XYChart.Series<>();

    @FXML
    public void initialize() {
        opinionChart.getYAxis().setLabel("Generals");
        opinionChart.getXAxis().setLabel("Step");
        ((NumberAxis) opinionChart.getYAxis()).setLowerBound(0);
        ((NumberAxis) opinionChart.getXAxis()).setLowerBound(0);
        ((NumberAxis) opinionChart.getXAxis()).setUpperBound(10);
        ((NumberAxis) opinionChart.getYAxis()).setUpperBound(10);

        ((NumberAxis) opinionChart.getYAxis()).setMinorTickLength(0);
        ((NumberAxis) opinionChart.getXAxis()).setMinorTickLength(0);

        ((NumberAxis) opinionChart.getYAxis()).setTickUnit(1);

        opinionChart.getYAxis().setAutoRanging(false);
        opinionChart.getXAxis().setAutoRanging(true);

        StringConverter<Number> onlyIntegers = new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.intValue() == number.doubleValue() ? String.valueOf(number.intValue()) : "";
            }

            @Override
            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        };

        ((NumberAxis) opinionChart.getYAxis()).setTickLabelFormatter(onlyIntegers);
        ((NumberAxis) opinionChart.getXAxis()).setTickLabelFormatter(onlyIntegers);

        supporting.setName("For attack");
        notSupporting.setName("For defense");
        opinionChart.getData().add(supporting);
        opinionChart.getData().add(notSupporting);
    }

    public void addStats(int numSupporting, int numNotSupporting) {
        Platform.runLater(
                () -> {
                    ((NumberAxis) opinionChart.getYAxis()).setUpperBound(numSupporting + numNotSupporting);
                    supporting.getData().add(new XYChart.Data<>(nextX, numSupporting));
                    notSupporting.getData().add(new XYChart.Data<>(nextX, numNotSupporting));
                    nextX++;
                }
        );

    }

    public void clear() {
        opinionChart.setAnimated(false);
        supporting.getData().clear();
        notSupporting.getData().clear();
        opinionChart.getData().clear();
        opinionChart.getData().add(supporting);
        opinionChart.getData().add(notSupporting);
        nextX = 1;
        opinionChart.setAnimated(true);
    }
}
