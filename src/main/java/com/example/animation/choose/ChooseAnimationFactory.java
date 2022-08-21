package com.example.animation.choose;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.util.Duration;
import lombok.Setter;

import java.util.function.Consumer;

public class ChooseAnimationFactory {
    @Setter
    private Duration duration = new Duration(2000);

    public Animation getChooseOpinionAnimation(Node node, Consumer<ActionEvent> chooseAction){
        ScaleTransition scaleUp = new ScaleTransition(duration, node);
        scaleUp.setToX(1.5);
        scaleUp.setToY(1.5);

        scaleUp.setOnFinished(chooseAction::accept);

        ScaleTransition scaleDown = new ScaleTransition(duration, node);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        return new SequentialTransition(scaleUp, scaleDown);
    }
}
