package com.example.algorithm.report;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.example.algorithm.VertexRole;
import com.example.algorithm.operations.Operation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepReport {
    @Getter
    private Map<Vertex<Integer>, VertexRole> roles = new HashMap<>();
    @Getter
    private List<Operation> operations = new ArrayList<>();
}
