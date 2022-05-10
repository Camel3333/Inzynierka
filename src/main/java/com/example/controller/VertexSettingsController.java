package com.example.controller;

import com.example.model.Agent;
import com.example.model.AgentOpinion;
import javafx.fxml.FXML;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/view/vertexSettingsView.fxml")
public class VertexSettingsController {
    @FXML
    private ToggleSwitch traitorSwitch;
    @FXML
    private CheckComboBox<AgentOpinion> opinionsBox;

    public void bindVertex(Agent agent){
        traitorSwitch.selectedProperty().bindBidirectional(agent.isTraitor());
        // TODO: bind opinionsBox with agent opinions
//        opinionsBox.getItems().addAll(vertex.getOpinions().getOpinions());
//        opinionsBox. .bindBidirectional(vertex.getSupportsOpinion());
    }

}
