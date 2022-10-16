package com.example.information;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.AlgorithmPhase;
import com.example.algorithm.AlgorithmType;
import com.example.algorithm.VertexRole;
import com.example.algorithm.report.StepReport;
import com.example.information.printer.InformationPrinter;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class KingInformationEngine implements InformationEngine {
    private InformationPrinter informationPrinter;

    @Override
    public void processReport(StepReport stepReport) {
        informationPrinter.setAlgorithmName(AlgorithmType.KING.name());
        informationPrinter.setAlgorithmPhase(stepReport.getAlgorithmPhase());
        informationPrinter.setStepDescription(generateDescription(stepReport));
        informationPrinter.listProperties(generateProperties(stepReport));
        informationPrinter.renderView();
    }

    private Map<String, String> generateProperties(StepReport stepReport) {
        Map<String, String> properties = stepReport.getProperties();
        Map<String, String> propertiesToSend = new HashMap<>();
        propertiesToSend.put("phase", properties.get("phase"));
        Optional<Map.Entry<Vertex<Integer>, VertexRole>> kingEntry = stepReport.getRoles().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == VertexRole.KING)
                .findFirst();
        kingEntry.ifPresent(entry -> {
            propertiesToSend.put("king", String.valueOf(entry.getKey().element()));
        });
        return propertiesToSend;
    }

    private String generateDescription(StepReport stepReport) {
        return stepReport.getAlgorithmPhase() == AlgorithmPhase.SEND ? generateDescriptionForSend() : generateDescriptionForChoose();
    }

    private String generateDescriptionForSend() {
        return "At send step all generals send their opinions to other generals.";
    }

    private String generateDescriptionForChoose() {
        return "At choose step each general is making decision. Decision is based on the majority of received opinions. If number of votes is less than (⌊ n / 2 ⌋ + t), general asks king for his opinion.";
    }
}
