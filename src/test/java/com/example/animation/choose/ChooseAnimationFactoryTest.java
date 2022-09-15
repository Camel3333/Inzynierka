package com.example.animation.choose;

import javafx.animation.Animation;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChooseAnimationFactoryTest {
    private JFXPanel panel = new JFXPanel();
    private ChooseAnimationFactory chooseAnimationFactory;

    @BeforeEach
    public void init() {
        chooseAnimationFactory = new ChooseAnimationFactory(new Duration(1500));
    }

    @Test
    public void getChooseAnimationTest() {
        // Given
        Node node = new Circle();

        // When
        Animation chooseAnimation = chooseAnimationFactory.getChooseOpinionAnimation(node, e -> {});

        // Then
        assertNotNull(chooseAnimation);
    }
}
