package com.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyVertexTest {

    @Test
    public void receiveOpinionTest(){
        // Given
        var v1 = new MyVertex<>(1);

        var o1 = new AgentOpinion("name1", true);
        var o2 = new AgentOpinion("name2", false);

        // When
        v1.receiveOpinion(o1);
        v1.receiveOpinion(o2);

        // Then
        assertEquals(v1.getKnowledge().get(0), o1);
        assertEquals(v1.getKnowledge().get(1), o2);
    }

    @Test
    public void getNextOpinionTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);
        var v3 = new MyVertex<>(3);

        var o1 = new AgentOpinion("opinion", false);

        // When
        v1.setOpinion(o1);
        v1.setIsTraitor(false);

        var o2 = v1.getNextOpinion(v2);
        var o3 = v1.getNextOpinion(v3);

        // Then
        assertTrue(o2.compareOpinion(o1));
        assertTrue(o3.compareOpinion(o1));
    }

    @Test
    public void getNextOpinionFromTraitorTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var v2 = new MyVertex<>(2);
        var v3 = new MyVertex<>(3);

        var o1 = new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);

        // When
        v1.setOpinion(o1);
        v1.setIsTraitor(true);

        var o3 = v1.getNextOpinion(v2);
        var o4 = v1.getNextOpinion(v3);

        // Then
        assertTrue(o3.compareOpinion(o2));
        assertTrue(o4.compareOpinion(o1));
    }

    @Test
    public void getMajorityVoteTest(){
        // Given
        var v1 = new MyVertex<>(1);

        var o1 =  new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);
        var o3 = new AgentOpinion("opinion", true);
        var o4 = new AgentOpinion("opinion", true);

        // When
        v1.receiveOpinion(o1);
        v1.receiveOpinion(o2);
        v1.receiveOpinion(o3);
        v1.receiveOpinion(o4);

        // Then
        assertTrue(v1.getMajorityVote());
    }

    @Test
    public void getMajorityVoteWithEmptyKnowledgeTest(){
        // Given
        var v1 = new MyVertex<>(1);

        // When

        // Then
        assertFalse(v1.getMajorityVote());
    }

    @Test
    public void getMajorityVoteCountTest(){
        // Given
        var v1 = new MyVertex<>(1);

        var o1 =  new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);
        var o3 = new AgentOpinion("opinion", true);
        var o4 = new AgentOpinion("opinion", true);

        // When
        v1.receiveOpinion(o1);
        v1.receiveOpinion(o2);
        v1.receiveOpinion(o3);
        v1.receiveOpinion(o4);

        // Then
        assertEquals(3, v1.getMajorityVoteCount());
    }

    @Test
    public void getMajorityVoteCountWithEmptyKnowledgeTest(){
        // Given
        var v1 = new MyVertex<>(1);

        // When

        // Then
        assertEquals(0, v1.getMajorityVoteCount());
    }

    @Test
    public void chooseMajorityTest(){
        // Given
        var v1 = new MyVertex<>(1);

        var o1 =  new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);
        var o3 = new AgentOpinion("opinion", true);
        var o4 = new AgentOpinion("opinion", true);

        // When
        v1.receiveOpinion(o1);
        v1.receiveOpinion(o2);
        v1.receiveOpinion(o3);
        v1.receiveOpinion(o4);

        v1.chooseMajority();

        // Then
        assertTrue(v1.getOpinion().isSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithEmptyKnowledgeTest(){
        // Given
        var v1 = new MyVertex<>(1);
        var o1 = new AgentOpinion("opinion", false);

        // When
        v1.setOpinion(o1);
        v1.chooseMajority();

        // Then
        assertFalse(v1.getOpinion().isSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithTieBreakerTest(){
        var v1 = new MyVertex<>(1);

        var o1 =  new AgentOpinion("opinion", false);
        var o2 = new AgentOpinion("opinion", true);
        var o3 = new AgentOpinion("opinion", true);
        var o4 = new AgentOpinion("opinion", false);

        // When
        v1.receiveOpinion(o1);
        v1.receiveOpinion(o2);
        v1.receiveOpinion(o3);

        v1.chooseMajorityWithTieBreaker(o4, 1);

        // Then
        assertTrue(v1.getOpinion().isSupporting().getValue());
    }

    @Test
    public void chooseMajorityWithTieBreakerAndTieTest(){
        var v1 = new MyVertex<>(1);

        var o1 =  new AgentOpinion("opinion", true);
        var o2 = new AgentOpinion("opinion", false);
        var o3 = new AgentOpinion("opinion", true);

        // When
        v1.receiveOpinion(o1);
        v1.receiveOpinion(o2);

        v1.chooseMajorityWithTieBreaker(o3, 1);

        // Then
        assertTrue(v1.getOpinion().isSupporting().getValue());
    }
}
