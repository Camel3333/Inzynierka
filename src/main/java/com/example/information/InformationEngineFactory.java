package com.example.information;

import com.example.algorithm.AlgorithmType;
import com.example.information.printer.InformationPrinter;

public class InformationEngineFactory {
    public static InformationEngine createForAlgorithm(AlgorithmType algorithmType, InformationPrinter informationPrinter) {
        switch (algorithmType) {
            case LAMPORT -> {
                return new LamportInformationEngine(informationPrinter);
            }
            case KING -> {
                return new KingInformationEngine(informationPrinter);
            }
            case QVOTER -> {
                return new QVoterInformationEngine(informationPrinter);
            }
        }
        throw new IllegalArgumentException("There is no InformationEngine for " + algorithmType);
    }
}
