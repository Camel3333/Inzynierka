package com.example.information;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.AlgorithmPhase;
import com.example.algorithm.AlgorithmType;
import com.example.algorithm.VertexRole;
import com.example.algorithm.report.StepReport;
import com.example.information.printer.InformationPrinter;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class QVoterInformationEngine implements InformationEngine {
    private InformationPrinter informationPrinter;

    @Override
    public void processReport(StepReport stepReport) {
        informationPrinter.setAlgorithmName(AlgorithmType.QVOTER.name());
        informationPrinter.setAlgorithmPhase(stepReport.getAlgorithmPhase());
        informationPrinter.setStepDescription(generateDescription(stepReport));
        informationPrinter.listProperties(generateProperties(stepReport));
        informationPrinter.renderView();
    }

    private Map<String, String> generateProperties(StepReport stepReport) {
        Map<String, String> properties = stepReport.getProperties();
        Map<String, String> propertiesToSend = new HashMap<>();
        propertiesToSend.put("time", properties.get("time"));
        Optional<Integer> voterId = stepReport.getRoles().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == VertexRole.VOTER)
                .map(Map.Entry::getKey)
                .map(Vertex::element)
                .findFirst();
        List<Integer> neighbours = stepReport.getRoles().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == VertexRole.NEIGHBOUR)
                .map(Map.Entry::getKey)
                .map(Vertex::element)
                .toList();
        voterId.ifPresent(voter -> {
            propertiesToSend.put("voter", String.valueOf(voter));
            propertiesToSend.put("neighbours", neighbours.toString());
        });
        return propertiesToSend;
    }

    private String generateDescription(StepReport stepReport) {
        return stepReport.getAlgorithmPhase() == AlgorithmPhase.SEND ? generateDescriptionForSend() : generateDescriptionForChoose();
    }

    private String generateDescriptionForSend() {
        return "At send step all neighbours send their opinions to voter.";
    }

    private String generateDescriptionForChoose() {
        return "At choose step voter makes decision. Decision is based on the majority of received opinions. If there is a draw, voter uses probability to either choose on of the decisions or stick to his opinion.";
    }
}
