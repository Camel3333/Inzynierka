package com.example.model;

import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MyGraphTest {
    private MyGraph<Integer, Integer> graph;

    @BeforeEach
    public void resetGraph(){
        graph = new MyGraph<>();
    }

    @Test
    public void insertVertexTest(){
        // Given

        // When
        var v1 = graph.insertVertex(1);

        // Then
        assertTrue(graph.vertices().contains(v1));
    }

    @Test
    public void insertEdgeTest(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);

        // When
        var e1 = graph.insertEdge(v1, v2, 1);
        var e2 = graph.insertEdge(1, 2, 1);

        // Then
        assertTrue(graph.edges().contains(e1));
        assertTrue(graph.edges().contains(e2));
    }

    @Test
    public void insertingEdgeForNonExistingVerticesShouldFail(){
        assertThrows(InvalidVertexException.class, () -> graph.insertEdge(1, 2, 1));
    }

    @Test
    public void numVerticesAndEdgesTest(){
        // Given

        // When
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        graph.insertEdge(v1,v2,1);

        // Then
        assertEquals(2, graph.numVertices());
        assertEquals(1, graph.numEdges());
    }

    @Test
    public void addingTwoVerticesWithSameElementShouldFail(){
        // Given
        var v1 = graph.insertVertex(1);

        //Then
        assertThrows(InvalidVertexException.class, () -> graph.insertVertex(1));
    }

    @Test
    public void insertTwoEdgesBetweenSameVertices(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);

        // When
        graph.insertEdge(v1,v2,1);
        graph.insertEdge(v1,v2,2);

        // Then
        assertEquals(2, graph.numEdges());
    }

    @Test
    public void shouldHave0IncidentEdges(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);

        // When
        graph.insertEdge(v1,v3,1);

        // Then
        assertEquals(0, graph.incidentEdges(v2).size());
    }

    @Test
    public void shouldHave1IncidentEdge(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);

        // When
        var e1 = graph.insertEdge(v1,v2,1);

        // Then
        assertEquals(1, graph.incidentEdges(v1).size());
        assertEquals(1, graph.incidentEdges(v2).size());
        assertTrue(graph.incidentEdges(v1).contains(e1));
        assertTrue(graph.incidentEdges(v2).contains(e1));
    }

    @Test
    public void shouldHave3IncidentEdge(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);

        // When
        graph.insertEdge(v1,v3,1);
        graph.insertEdge(v1,v3,2);
        graph.insertEdge(v1,v2,3);

        // Then
        assertEquals(3, graph.incidentEdges(v1).size());
        assertEquals(2, graph.incidentEdges(v3).size());
        assertEquals(1, graph.incidentEdges(v2).size());
    }

    @Test
    public void adjacencyTest(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);

        // When
        graph.insertEdge(v1,v3,1);
        graph.insertEdge(v1,v2,2);

        // Then
        assertTrue(graph.areAdjacent(v1, v3));
        assertTrue(graph.areAdjacent(v1, v2));
        assertFalse(graph.areAdjacent(v2, v3));
    }

    @Test
    public void oppositeVertexTest(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);

        // When
        var e1 = graph.insertEdge(v1,v3,1);
        var e2 = graph.insertEdge(v1,v2,2);

        // Then
        assertEquals(v3, graph.opposite(v1, e1));
        assertEquals(v2, graph.opposite(v1, e2));
    }

    @Test
    public void edgesBetweenTest(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);

        // When
        var e1 = graph.insertEdge(v1, v2, 1);
        var e2 = graph.insertEdge(v1, v2, 2);
        var e3 = graph.insertEdge(v1, v3, 3);

        // Then
        assertEquals(2, graph.edgesBetween(v1, v2).size());
        assertTrue(graph.edgesBetween(v1, v2).containsAll(Arrays.asList(e1, e2)));
        assertEquals(1, graph.edgesBetween(v1, v3).size());
        assertTrue(graph.edgesBetween(v1, v3).contains(e3));
        assertEquals(0, graph.edgesBetween(v2, v3).size());
    }

    @Test
    public void vertexNeighboursTest(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);

        // When
        var e1 = graph.insertEdge(v1, v2, 1);
        var e2 = graph.insertEdge(v1, v2, 2);
        var e3 = graph.insertEdge(v1, v3, 3);

        // Then
        assertEquals(2, graph.vertexNeighbours(v1).size());
        assertTrue(graph.vertexNeighbours(v1).containsAll(Arrays.asList(v2,v3)));
        assertEquals(1, graph.vertexNeighbours(v2).size());
        assertTrue(graph.vertexNeighbours(v2).contains(v1));
        assertEquals(1, graph.vertexNeighbours(v3).size());
        assertTrue(graph.vertexNeighbours(v3).contains(v1));
    }

    @Test
    public void removeVertexWithNoNeighbours(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);
        graph.insertEdge(v2, v3 ,1);

        // When
        graph.removeVertex(v1);

        // Then
        assertEquals(0, graph.incidentEdges(v1).size());
        assertEquals(1, graph.incidentEdges(v2).size());
        assertEquals(1, graph.incidentEdges(v3).size());
        assertFalse(graph.vertices().contains(v1));
        assertThrows(InvalidVertexException.class, () -> graph.vertexNeighbours(v1));
    }

    @Test
    public void removeVertexWithNeighbours(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var v3 = graph.insertVertex(3);
        graph.insertEdge(v1, v2 ,1);
        graph.insertEdge(v1, v3 ,1);

        // When
        graph.removeVertex(v1);

        // Then
        assertEquals(0, graph.incidentEdges(v1).size());
        assertEquals(0, graph.incidentEdges(v2).size());
        assertEquals(0, graph.incidentEdges(v3).size());
        assertFalse(graph.vertices().contains(v1));
        assertThrows(InvalidVertexException.class, () -> graph.vertexNeighbours(v1));
        assertThrows(InvalidVertexException.class, () -> graph.areAdjacent(v1, v2));
    }

    @Test
    public void removeEdgeTest(){
        // Given
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var e1 = graph.insertEdge(v1, v2 ,1);

        // When
        graph.removeEdge(e1);

        // Then
        assertEquals(0, graph.numEdges());
        assertFalse(graph.edges().contains(e1));
        assertFalse(graph.incidentEdges(v1).contains(e1));
        assertFalse(graph.incidentEdges(v2).contains(e1));
        assertFalse(graph.edgesBetween(v1, v2).contains(e1));
    }

    @Test
    public void replaceElementInVertexTest(){
        // Given
        var startElement = 1;
        var v1 = graph.insertVertex(startElement);
        var newElement = 2;

        // When
        var oldElement = graph.replace(v1, newElement);

        // Then
        assertEquals(startElement, oldElement);
        assertEquals(newElement, v1.element());
    }

    @Test
    public void replaceElementInEdgeTest(){
        // Given
        var startElement = 1;
        var v1 = graph.insertVertex(1);
        var v2 = graph.insertVertex(2);
        var e1 = graph.insertEdge(v1, v2, startElement);
        var newElement = 2;

        // When
        var oldElement = graph.replace(e1, newElement);

        // Then
        assertEquals(startElement, oldElement);
        assertEquals(newElement, e1.element());
    }

}
