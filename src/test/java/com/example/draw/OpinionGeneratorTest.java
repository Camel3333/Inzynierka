package com.example.draw;

import com.example.model.MyGraph;
import com.example.model.MyVertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpinionGeneratorTest {
    private OpinionGenerator opinionGenerator;

    @BeforeEach
    public void initTraitorsGenerator(){
        opinionGenerator = new OpinionGenerator();
    }

    @Test
    public void shouldSetZeroTraitors(){
        // Given
        MyGraph<Integer,Integer> exampleGraph = generateFullGraph(4);
        double percentage = 0.0;
        long expectedTraitorsNumber = 0;

        // When
        opinionGenerator.generateAttackers(exampleGraph, percentage);

        // Then
        assertEquals(countAttackers(exampleGraph), expectedTraitorsNumber);
    }

    @Test
    public void shouldSetAllTraitors(){
        // Given
        MyGraph<Integer,Integer> exampleGraph = generateFullGraph(4);
        double percentage = 1.0;
        long expectedTraitorsNumber = 4;

        // When
        opinionGenerator.generateAttackers(exampleGraph, percentage);

        // Then
        assertEquals(countAttackers(exampleGraph), expectedTraitorsNumber);
    }

    @Test
    public void shouldSetTraitors(){
        // Given
        MyGraph<Integer,Integer> exampleGraph = generateFullGraph(4);
        double percentage = 0.73;
        long expectedTraitorsNumber = 2;

        // When
        opinionGenerator.generateAttackers(exampleGraph, percentage);

        // Then
        assertEquals(countAttackers(exampleGraph), expectedTraitorsNumber);
    }

    @Test
    public void shouldSetGivenNumberOfTraitorsWhenThereAreTraitorsAlready(){
        // Given
        MyGraph<Integer,Integer> exampleGraph = generateFullGraph(4);
        exampleGraph.vertices()
                .stream()
                .map(vertex -> (MyVertex<Integer>)vertex)
                .forEach(integerMyVertex -> integerMyVertex.setIsTraitor(true));
        double percentage = 0.25;
        long expectedTraitorsNumber = 1;

        // When
        opinionGenerator.generateAttackers(exampleGraph, percentage);

        // Then
        assertEquals(countAttackers(exampleGraph), expectedTraitorsNumber);
    }

    private MyGraph<Integer, Integer> generateFullGraph(int numberOfVertices){
        MyGraph<Integer, Integer> graph = new MyGraph<>();
        int edgeId = 1;

        for (int i = 1; i <= numberOfVertices; i++){
            graph.insertVertex(i);
        }

        for (int i = 1; i <= numberOfVertices; i++){
            for (int j = i+1; j <= numberOfVertices; j++){
                graph.insertEdge(i, j, edgeId++);
            }
        }
        return graph;
    }

    private long countAttackers(MyGraph<Integer,Integer> graph){
        return graph.vertices()
                .stream()
                .map(integerVertex -> (MyVertex<Integer>)integerVertex)
                .filter(agent -> agent.isSupportingOpinion().get())
                .count();
    }
}
