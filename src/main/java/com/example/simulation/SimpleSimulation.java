package com.example.simulation;

import com.example.algorithm.Algorithm;
import com.example.settings.AlgorithmSettings;
import com.example.controller.GraphController;
import com.example.model.MyGraph;
import lombok.Setter;

public class SimpleSimulation implements Simulation {

    /*
    STEP -> zwraca klasę reprezentująca zmiany
    Po wykonaniu kroku wizualizujemy zmiany - tylko jeśli aktulany tryb symulacji tego wymaga
     */

    @Setter
    private GraphController graphController;

    public SimpleSimulation(GraphController graphController) {
        this.graphController = graphController;
    }

    public void nextStep(){}

    @Override
    public void start(Algorithm algorithm, AlgorithmSettings settings) {
        // while {
        // wykonuj kroki na algorytmie, dopóki nic się nie zmieni
        // algorithm.step()
        // zwizualizuj zmiany
        // zawieś się dopóki użytkonik nie kliknie next /
        // odczekaj 2s i wykonaj kolejny krok
        // }
        algorithm.execute((MyGraph<Integer, Integer>) graphController.getGraph(), settings);
        graphController.update();
    }
}
