package com.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyVertexTest {

    @Test
    public void receiveOpinionsTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);

        var opinions = new AgentOpinions();

        var o1 = new AgentOpinion("name", true);

        // When
        opinions.addOpinion(o1);

        v2.receiveOpinions(v1, opinions);

        // Then
        assertEquals(v2.getKnowledge().get(v1), opinions);
    }

    @Test
    public void receiveOpinionsTwiceFromSameVertexTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);

        var opinions1 = new AgentOpinions();
        var opinions2 = new AgentOpinions();

        var o1 = new AgentOpinion("opinion1", true);
        var o2 = new AgentOpinion("opinion2", false);

        // When
        opinions1.addOpinion(o1);
        opinions2.addOpinion(o2);

        v2.receiveOpinions(v1, opinions1);
        v2.receiveOpinions(v1, opinions2);

        // Then
        assertEquals(v2.getKnowledge().get(v1), opinions2);
    }

    @Test
    public void sendOpinionsTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);

        var opinions = new AgentOpinions();

        var o1 = new AgentOpinion("opinion", false);

        // When
        opinions.addOpinion(o1);

        v1.setOpinions(opinions);
        v1.setIsTraitor(false);
        v1.sendOpinions(v2);

        // Then
        assertTrue(v2.getKnowledge().get(v1).compareOpinions(opinions));
    }

    @Test
    public void sendOpinionsFromTraitorTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);

        var opinions = new AgentOpinions();
        var expectedOpinions = new AgentOpinions();

        var o1 = new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);

        // When
        opinions.addOpinion(o1);
        expectedOpinions.addOpinion(o2);

        v1.setOpinions(opinions);
        v1.setIsTraitor(true);
        v1.sendOpinions(v2);

        // Then
        assertTrue(v2.getKnowledge().get(v1).compareOpinions(expectedOpinions));
    }

    @Test
    public void chooseMajorityTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);
        var v3 = new MyVertex<>(3);
        var v4 = new MyVertex<>(4);

        var opinions1 = new AgentOpinions();
        var opinions2 = new AgentOpinions();
        var opinions3 = new AgentOpinions();
        var opinions4 = new AgentOpinions();

        var o1 =  new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);
        var o3 = new AgentOpinion("opinion", false);
        var o4 = new AgentOpinion("opinion", true);

        // When
        opinions1.addOpinion(o1);
        opinions2.addOpinion(o2);
        opinions3.addOpinion(o3);
        opinions4.addOpinion(o4);

        v4.setOpinions(opinions4);

        v4.receiveOpinions(v1, opinions1);
        v4.receiveOpinions(v2, opinions2);
        v4.receiveOpinions(v3, opinions3);

        v4.chooseMajority();

        // Then
        assertFalse(v4.getOpinions().getOpinionByName("opinion").isSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithEmptyKnowledgeTest(){
        // Given
        var v1 = new MyVertex<>(1);

        var opinions = new AgentOpinions();

        var o1 = new AgentOpinion("opinion", true);

        // When
        opinions.addOpinion(o1);

        v1.setOpinions(opinions);
        v1.chooseMajority();

        // Then
        assertFalse(v1.getOpinions().getOpinionByName("opinion").isSupporting().getValue());
    }
}
