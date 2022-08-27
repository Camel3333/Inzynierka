package com.example.animation.send;

import javafx.animation.PathTransition;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SendAnimationFactoryTest {
    private JFXPanel panel = new JFXPanel(); //allows to run test with JavaFX objects
    private SendAnimationFactory sendAnimationFactory = new SendAnimationFactory(new Duration(1500));

    @BeforeEach
    public void init(){
        sendAnimationFactory = new SendAnimationFactory(new Duration(1500));
    }

    @Test
    public void getAttackAnimationTest(){
        // Given
        Point2D from = new Point2D(0.0, 0.0);
        Point2D to = new Point2D(10.0, 10.0);

        // When
        PathTransition pathTransition = sendAnimationFactory.getAttackAnimation(from, to);

        // Then
        assertNotNull(pathTransition);
        assertNotNull(pathTransition.getNode());
        assertNotNull(pathTransition.getPath());
    }

    @Test
    public void getDefenseAnimationTest(){
        // Given
        Point2D from = new Point2D(0.0, 0.0);
        Point2D to = new Point2D(10.0, 10.0);

        // When
        PathTransition pathTransition = sendAnimationFactory.getDefenseAnimation(from, to);

        // Then
        assertNotNull(pathTransition);
        assertNotNull(pathTransition.getNode());
        assertNotNull(pathTransition.getPath());
    }
}
