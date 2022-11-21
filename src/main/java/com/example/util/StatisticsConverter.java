package com.example.util;

import javafx.scene.chart.XYChart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatisticsConverter {
    static public void exportStats(File file, XYChart.Series<Number, Number> supporting) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("turn,supporting\n");
        for (int i = 0; i < supporting.getData().size(); i++) {
            writer.write(i+","+supporting.getData().get(i).getYValue()+"\n");
        }
        writer.close();
    }
}
