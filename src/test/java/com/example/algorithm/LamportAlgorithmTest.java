package com.example.algorithm;

import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LamportAlgorithmTest {
    private MyGraph<Integer, Integer> graph;
    private final Algorithm algorithm = new LamportAlgorithm();

    @BeforeEach
    public void resetGraph(){
        graph = new MyGraph<>();
    }

    @Test
    public void completeGraphWithOneTraitorTest(){
        // Given
        var v1 = (MyVertex<Integer>) graph.insertVertex(1);
        var v2 = (MyVertex<Integer>) graph.insertVertex(2);
        var v3 = (MyVertex<Integer>) graph.insertVertex(3);
        var v4 = (MyVertex<Integer>) graph.insertVertex(4);

        var e1 = graph.insertEdge(v1, v2, 1);
        var e2 = graph.insertEdge(v2, v3, 2);
        var e3 = graph.insertEdge(v3, v4, 3);
        var e4 = graph.insertEdge(v4, v1, 4);
        var e5 = graph.insertEdge(v1, v3, 5);
        var e6 = graph.insertEdge(v2, v4, 6);

        var opinions1 = new AgentOpinions();
        var opinions2 = new AgentOpinions();
        var opinions3 = new AgentOpinions();
        var opinions4 = new AgentOpinions();

        var o1 = new AgentOpinion("name", false);
        var o2 = new AgentOpinion("name", true);
        var o3 = new AgentOpinion("name", false);
        var o4 = new AgentOpinion("name", true);

        // When
        v1.setIsTraitor(true);
        v2.setIsTraitor(false);
        v3.setIsTraitor(false);
        v4.setIsTraitor(false);

        opinions1.addOpinion(o1);
        opinions2.addOpinion(o2);
        opinions3.addOpinion(o3);
        opinions4.addOpinion(o4);

        v1.setOpinions(opinions1);
        v2.setOpinions(opinions2);
        v3.setOpinions(opinions3);
        v4.setOpinions(opinions4);

        algorithm.execute(graph, 2);

        // Then
        assertTrue(graph.checkConsensus());
    }

    @Test
    public void twoRegularGraphWithOneTraitorTest(){
        // Given
        var v1 = (MyVertex<Integer>) graph.insertVertex(1);
        var v2 = (MyVertex<Integer>) graph.insertVertex(2);
        var v3 = (MyVertex<Integer>) graph.insertVertex(3);
        var v4 = (MyVertex<Integer>) graph.insertVertex(4);
        var v5 = (MyVertex<Integer>) graph.insertVertex(5);
        var v6 = (MyVertex<Integer>) graph.insertVertex(6);

        var e1 = graph.insertEdge(v1, v2, 1);
        var e2 = graph.insertEdge(v2, v3, 2);
        var e3 = graph.insertEdge(v3, v4, 3);
        var e4 = graph.insertEdge(v4, v5, 4);
        var e5 = graph.insertEdge(v5, v6, 5);
        var e6 = graph.insertEdge(v6, v1, 6);
        var e7 = graph.insertEdge(v1, v4, 7);
        var e8 = graph.insertEdge(v2, v5, 8);
        var e9 = graph.insertEdge(v3, v6, 9);

        var opinions1 = new AgentOpinions();
        var opinions2 = new AgentOpinions();
        var opinions3 = new AgentOpinions();
        var opinions4 = new AgentOpinions();
        var opinions5 = new AgentOpinions();
        var opinions6= new AgentOpinions();

        var o1 = new AgentOpinion("name", false);
        var o2 = new AgentOpinion("name", true);
        var o3 = new AgentOpinion("name", false);
        var o4 = new AgentOpinion("name", true);
        var o5 = new AgentOpinion("name", false);
        var o6 = new AgentOpinion("name", false);

        // When
        v1.setIsTraitor(true);
        v2.setIsTraitor(false);
        v3.setIsTraitor(false);
        v4.setIsTraitor(false);
        v5.setIsTraitor(false);
        v6.setIsTraitor(false);

        opinions1.addOpinion(o1);
        opinions2.addOpinion(o2);
        opinions3.addOpinion(o3);
        opinions4.addOpinion(o4);
        opinions5.addOpinion(o5);
        opinions6.addOpinion(o6);

        v1.setOpinions(opinions1);
        v2.setOpinions(opinions2);
        v3.setOpinions(opinions3);
        v4.setOpinions(opinions4);
        v5.setOpinions(opinions5);
        v6.setOpinions(opinions6);

        algorithm.execute(graph, 2);

        // Then
        assertTrue(graph.checkConsensus());
    }

    @Test
    public void emptyGraphTest(){
        // Given

        // When

        // Then
        assertTrue(graph.checkConsensus());
    }

}
