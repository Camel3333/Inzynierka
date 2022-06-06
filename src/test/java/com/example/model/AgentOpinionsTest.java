package com.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgentOpinionsTest {

    @Test
    public void addOpinionsTest(){
        // Given
        var opinions = new AgentOpinions();

        var o1 = new AgentOpinion("name1", true);
        var o2 = new AgentOpinion("name2", false);

        // When
        opinions.addOpinion(o1);
        opinions.addOpinion(o2);

        // Then
        assertEquals(2, opinions.getOpinions().size());
        assertEquals(o1, opinions.getOpinionByName("name1"));
        assertEquals(o2, opinions.getOpinionByName("name2"));
    }


    @Test
    public void addOpinionsWithSameNameTest(){
        // Given
        var opinions = new AgentOpinions();

        var o1 = new AgentOpinion("name", true);
        var o2 = new AgentOpinion("name", false);

        // When
        opinions.addOpinion(o1);
        opinions.addOpinion(o2);

        // Then
        assertEquals(1, opinions.getOpinions().size());
        assertEquals(o1, opinions.getOpinionByName("name"));
    }

    @Test
    public void removeOpinionsTest(){
        // Given
        var opinions = new AgentOpinions();

        var o1 = new AgentOpinion("name1", true);
        var o2 = new AgentOpinion("name2", true);

        // When
        opinions.addOpinion(o1);
        opinions.addOpinion(o2);
        opinions.removeOpinion(o2);

        // Then
        assertEquals(1, opinions.getOpinions().size());
        assertEquals(o1, opinions.getOpinionByName("name1"));
        assertNull(opinions.getOpinionByName("name2"));
    }


    @Test
    public void compareOpinionsTest(){
        // Given
        var opinions1 = new AgentOpinions();
        var opinions2 = new AgentOpinions();
        var opinions3 = new AgentOpinions();

        var o1 = new AgentOpinion("name1", true);
        var o2 = new AgentOpinion("name1", true);
        var o3 = new AgentOpinion("name2", false);

        // When
        opinions1.addOpinion(o1);
        opinions1.addOpinion(o3);
        opinions2.addOpinion(o2);
        opinions2.addOpinion(o3);
        opinions3.addOpinion(o1);

        // Then
        assertTrue(opinions1.compareOpinions(opinions2));
        assertFalse(opinions1.compareOpinions(opinions3));
        assertFalse(opinions3.compareOpinions(opinions2));
    }

}
