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
public class LamportInformationEngine implements InformationEngine {
    private InformationPrinter informationPrinter;

    @Override
    public void processReport(StepReport stepReport) {
        informationPrinter.setAlgorithmName(AlgorithmType.LAMPORT.name());
        informationPrinter.setAlgorithmPhase(stepReport.getAlgorithmPhase());
        informationPrinter.setStepDescription(generateDescription(stepReport));
        informationPrinter.listProperties(generateProperties(stepReport));
        informationPrinter.renderView();
    }

    private String generateDescription(StepReport stepReport) {
        return stepReport.getAlgorithmPhase() == AlgorithmPhase.SEND ? generateDescriptionForSend() : generateDescriptionForChoose();
    }

    private Map<String, String> generateProperties(StepReport stepReport) {
        Map<String, String> properties = stepReport.getProperties();
        Map<String, String> propertiesToSend = new HashMap<>();
        propertiesToSend.put("depth", properties.get("depth"));
        Optional<Map.Entry<Vertex<Integer>, VertexRole>> commanderEntry = stepReport.getRoles().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == VertexRole.COMMANDER)
                .findFirst();
        commanderEntry.ifPresent(entry -> {
            propertiesToSend.put("commander", String.valueOf(entry.getKey().element()));
            propertiesToSend.put("lieutenants", "Vertices with green stroke");
        });
        return propertiesToSend;
    }

    private String generateDescriptionForSend() {
        return "At send step, commander sends an opinion to his lieutenants. When lieutenants receive information, they save it in the knowledge table.";
    }

    private String generateDescriptionForChoose() {
        return "At choose step, each lieutenant takes majority from his knowledge table and sets the result as his decision.";
    }
}
