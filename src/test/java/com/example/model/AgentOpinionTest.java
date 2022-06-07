package com.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentOpinionTest {

    @Test
    public void compareOpinionTest(){
        // Given
        var o1 = new AgentOpinion("name1", true);
        var o2 = new AgentOpinion("name1", true);
        var o3 = new AgentOpinion("name1", false);
        var o4 = new AgentOpinion("name2", true);

        // When

        // Then
        assertTrue(o1.compareOpinion(o2));
        assertFalse(o1.compareOpinion(o3));
        assertFalse(o1.compareOpinion(o4));
    }
}
