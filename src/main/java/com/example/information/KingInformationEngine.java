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
        propertiesToSend.put("round", properties.get("round"));
        Optional<Map.Entry<Vertex<Integer>, VertexRole>> kingEntry = stepReport.getRoles().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == VertexRole.KING)
                .findFirst();
        kingEntry.ifPresentOrElse(
                entry -> propertiesToSend.put("king", String.valueOf(entry.getKey().element())),
                () -> propertiesToSend.put("king", "-"));
        return propertiesToSend;
    }

    private String generateDescription(StepReport stepReport) {
        return stepReport.getAlgorithmPhase() == AlgorithmPhase.SEND ? generateDescriptionForSend(stepReport) : generateDescriptionForChoose(stepReport);
    }

    private String generateDescriptionForSend(StepReport stepReport) {
        return "At the first round, all generals send their opinions to other generals. After that, each general counts votes for attack and defense, and sets majority as his opinion.";
    }

    private String generateDescriptionForChoose(StepReport stepReport) {
        return "At the second round, king broadcasts his opinion to all generals. If number of votes for majority in previous round was less than "+stepReport.getProperties().get("accept king opinion condition")+", general accepts king's opinion.";
    }
}
