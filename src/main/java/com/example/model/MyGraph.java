package com.example.model;

import com.brunomnsilva.smartgraph.graph.*;
import javafx.beans.property.BooleanProperty;

import java.util.*;
import java.util.stream.Collectors;

public class MyGraph<V,E> implements Graph<V,E> {
    private Map vertices = new LinkedHashMap<>();
    private Map<Vertex<V>, List<Vertex<V>>> adjacency = new LinkedHashMap<>();
    private List<Edge<E, V>> edges = new ArrayList<>();

    public boolean checkConsensus(){
        if(numVertices() == 0){
            return true;
        }
        BooleanProperty expectedOpinion = ((MyVertex<V>) vertices().stream().toList().get(0)).getForAttack();
        return vertices().stream().allMatch(v -> ((MyVertex<V>) v).
                getForAttack().getValue() == expectedOpinion.getValue());
    }

    public int getTraitorsCount(){
        return (int) vertices().stream()
                .filter(v -> ((MyVertex<Integer>) v).isTraitor().getValue())
                .count();
    }

    @Override
    public int numVertices() {
        return adjacency.keySet().size();
    }

    @Override
    public int numEdges() {
        return edges.size();
    }

    @Override
    public Collection<Vertex<V>> vertices() {
        return adjacency.keySet();
    }

    @Override
    public Collection<Edge<E, V>> edges() {
        return edges;
    }

    @Override
    public Collection<Edge<E, V>> incidentEdges(Vertex<V> vertex) throws InvalidVertexException {
        return edges.stream()
                .filter(e -> ((MyEdge)e).contains(vertex))
                .toList();
    }

    @Override
    public Vertex<V> opposite(Vertex<V> v, Edge<E, V> e) throws InvalidVertexException, InvalidEdgeException {
        this.checkVertex(v);
        MyEdge edge = this.checkEdge(e);
        if (!edge.contains(v)) {
            return null;
        } else {
            return edge.getOpposite(v);
        }
    }

    @Override
    public boolean areAdjacent(Vertex<V> vertex, Vertex<V> vertex1) throws InvalidVertexException {
        checkVertex(vertex);
        checkVertex(vertex1);
        return adjacency.get(vertex).contains(vertex1);
    }

    public Collection<Edge<E,V>> edgesBetween(Vertex<V> v1, Vertex<V> v2){
        checkVertex(v1);
        checkVertex(v2);
        return edges.stream()
                .filter(e -> ((MyEdge) e).contains(v1) && ((MyEdge) e).contains(v2))
                .collect(Collectors.toList());
    }

    public Collection<Vertex<V>> vertexNeighbours(Vertex<V> v){
        checkVertex(v);
        return adjacency.get(v);
    }

    @Override
    public Vertex<V> insertVertex(V v) throws InvalidVertexException {
        if (this.existsVertexWith(v)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        } else {
            MyVertex<V> newVertex = new MyVertex<>(v);
            vertices.put(v, newVertex);
            adjacency.put(newVertex, new ArrayList<>());
            return newVertex;
        }
    }

    @Override
    public Edge<E, V> insertEdge(Vertex<V> v1, Vertex<V> v2, E e) throws InvalidVertexException, InvalidEdgeException {
        if (!(this.vertices().contains(v1) && this.vertices().contains(v2))){
            throw new InvalidVertexException("Both vertices must be a part of graph.");
        } else {
            MyEdge newEdge = new MyEdge(e, v1, v2);
            edges.add(newEdge);
            if (!adjacency.get(v1).contains(v2))
                adjacency.get(v1).add(v2);
            if (!adjacency.get(v2).contains(v1))
                adjacency.get(v2).add(v1);
            return newEdge;
        }
    }

    @Override
    public Edge<E, V> insertEdge(V v1, V v2, E e) throws InvalidVertexException, InvalidEdgeException {
        if (!(this.vertices.containsKey(v1) && this.vertices.containsKey(v2))){
            throw new InvalidVertexException("Both vertices must be a part of graph.");
        } else {
            MyEdge newEdge = new MyEdge(e, (Vertex<V>) vertices.get(v1), (Vertex<V>) vertices.get(v2));
            this.edges.add(newEdge);
            if (!adjacency.get(vertices.get(v1)).contains(vertices.get(v2)))
                adjacency.get(vertices.get(v1)).add(vertices.get(v2));
            if (!adjacency.get(vertices.get(v2)).contains(vertices.get(v1)))
                adjacency.get(vertices.get(v2)).add(vertices.get(v1));
            return newEdge;
        }
    }

    @Override
    public V removeVertex(Vertex<V> v) throws InvalidVertexException {
        this.checkVertex(v);
        // remove all edges containing vertex v
        var edgesContaining = incidentEdges(v);
        edges.removeAll(edgesContaining);

        // remove vertex from adjacency lists of all neighbours
        var neighbours = vertexNeighbours(v);
        neighbours.forEach(n -> adjacency.get(n).remove(v));

        //remove vertex from graph
        adjacency.remove(v);
        vertices.remove(v.element());

        return v.element();
    }

    @Override
    public E removeEdge(Edge<E, V> e) throws InvalidEdgeException {
        this.checkEdge(e);
        var vertices = e.vertices();
        // remove adjacency if this edge was the only one connecting two vertices
        var v1 = vertices[0];
        var v2 = vertices[1];
        var allEdges = edgesBetween(v1, v2);
        if (allEdges.size() == 1) {
            adjacency.get(v1).remove(v2);
            adjacency.get(v2).remove(v1);
        }

        // remove edge
        edges.remove(e);
        return e.element();
    }

    @Override
    public V replace(Vertex<V> vertex, V v) throws InvalidVertexException {
        if (this.existsVertexWith(v)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        } else {
            this.checkVertex(vertex);
            V oldElement = vertex.element();
            ((MyVertex<V>)vertex).setElement(v);
            vertices.remove(oldElement);
            vertices.put(v, vertex);
            return oldElement;
        }
    }

    @Override
    public E replace(Edge<E, V> edge, E e) throws InvalidEdgeException {
        this.checkEdge(edge);
        E oldElement = edge.element();
        ((MyEdge)edge).element = e;
        return oldElement;
    }

    public void insertVertex(Vertex<V> vertex) throws InvalidVertexException {
        if (this.existsVertexWith(vertex.element())) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }
        this.vertices.put(vertex.element(), vertex);
        adjacency.put(vertex, new ArrayList<>());
    }

    private boolean existsVertexWith(V vElement) {
        return this.vertices.containsKey(vElement);
    }

    //copied from GraphEdgeList
    private MyVertex<V> checkVertex(Vertex<V> v) throws InvalidVertexException {
        if (v == null) {
            throw new InvalidVertexException("Null vertex.");
        } else {
            MyVertex<V> vertex;
            try {
                vertex = (MyVertex<V>)v;
            } catch (ClassCastException var4) {
                throw new InvalidVertexException("Not a vertex.");
            }

            if (!this.vertices.containsKey(v.element())) {
                throw new InvalidVertexException("Vertex does not belong to this graph.");
            } else {
                return vertex;
            }
        }
    }

    //copied from GraphEdgeList
    private MyEdge checkEdge(Edge<E, V> e) throws InvalidEdgeException {
        if (e == null) {
            throw new InvalidEdgeException("Null edge.");
        } else {
            MyEdge edge;
            try {
                edge = (MyEdge) e;
            } catch (ClassCastException var4) {
                throw new InvalidVertexException("Not an adge.");
            }

            if (this.edges.stream().noneMatch(x -> ((MyEdge) x).element == edge.element)) {
                throw new InvalidEdgeException("Edge does not belong to this graph.");
            } else {
                return edge;
            }
        }
    }

    // copied from GraphEdgeList
    class MyEdge implements Edge<E, V> {
        E element;
        Vertex<V> vertexOutbound;
        Vertex<V> vertexInbound;

        public MyEdge(E element, Vertex<V> vertexOutbound, Vertex<V> vertexInbound) {
            this.element = element;
            this.vertexOutbound = vertexOutbound;
            this.vertexInbound = vertexInbound;
        }

        public E element() {
            return this.element;
        }

        public void setElement(E e) {
            element = e;
        }

        public boolean contains(Vertex<V> v) {
            return this.vertexOutbound == v || this.vertexInbound == v;
        }

        public Vertex<V>[] vertices() {
            Vertex[] vertices = new Vertex[]{this.vertexOutbound, this.vertexInbound};
            return vertices;
        }

        public Vertex<V> getOpposite(Vertex<V> v){
            if (! contains(v)) {
                throw new InvalidVertexException("There is no such vertex in the edge.");
            }
            else
                return v.equals(vertexInbound) ? vertexOutbound : vertexInbound;
        }

        public String toString() {
            return "Edge{{" + this.element + "}, vertexOutbound=" + this.vertexOutbound.toString() + ", vertexInbound=" + this.vertexInbound.toString() + '}';
        }
    }
}
