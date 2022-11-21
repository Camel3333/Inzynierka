package com.example.model;

import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyVertexTest {

    @Test
    public void receiveOpinionTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(false));

        // Then
        assertEquals(v1.getKnowledge().get(0), true);
        assertEquals(v1.getKnowledge().get(1), false);
    }

    @Test
    public void getNextOpinionTest() {
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);
        var v3 = new MyVertex<>(3);

        // When
        v1.setIsSupporting(false);
        v1.setIsTraitor(false);

        var o2 = v1.getNextOpinion(v2);
        var o3 = v1.getNextOpinion(v3);

        // Then
        assertEquals(o2.getValue(), false);
        assertEquals(o3.getValue(), false);
    }

    @Test
    public void getNextOpinionFromTraitorTest() {
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);
        var v3 = new MyVertex<>(3);

        // When
        v1.setIsSupporting(false);
        v1.setIsTraitor(true);

        var o3 = v1.getNextOpinion(v2);
        var o4 = v1.getNextOpinion(v3);

        // Then
        assertEquals(o3.getValue(), true);
        assertEquals(o4.getValue(), false);
    }

    @Test
    public void getMajorityVoteTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When
        v1.receiveOpinion(new SimpleBooleanProperty(false));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));

        // Then
        assertTrue(v1.getMajorityVote());
    }

    @Test
    public void getMajorityVoteWithEmptyKnowledgeTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When

        // Then
        assertFalse(v1.getMajorityVote());
    }

    @Test
    public void getMajorityVoteCountTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When
        v1.receiveOpinion(new SimpleBooleanProperty(false));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));

        // Then
        assertEquals(3, v1.getMajorityVoteCount());
    }

    @Test
    public void getMajorityVoteCountWithEmptyKnowledgeTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When

        // Then
        assertEquals(0, v1.getMajorityVoteCount());
    }

    @Test
    public void chooseMajorityTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When
        v1.receiveOpinion(new SimpleBooleanProperty(false));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));

        v1.chooseMajority();

        // Then
        assertTrue(v1.getIsSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithEmptyKnowledgeTest() {
        // Given
        var v1 = new MyVertex<>(1);

        // When
        v1.setIsSupporting(false);
        v1.chooseMajority();

        // Then
        assertFalse(v1.getIsSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithTieBreakerTest() {
        var v1 = new MyVertex<>(1);

        // When
        v1.receiveOpinion(new SimpleBooleanProperty(false));
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(true));

        v1.chooseMajorityWithTieBreaker(new SimpleBooleanProperty(false), 1);

        // Then
        assertTrue(v1.getIsSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithTieBreakerAndTieTest() {
        var v1 = new MyVertex<>(1);

        // When
        v1.receiveOpinion(new SimpleBooleanProperty(true));
        v1.receiveOpinion(new SimpleBooleanProperty(false));

        v1.chooseMajorityWithTieBreaker(new SimpleBooleanProperty(true), 1);

        // Then
        assertTrue(v1.getIsSupporting().getValue());
    }
}