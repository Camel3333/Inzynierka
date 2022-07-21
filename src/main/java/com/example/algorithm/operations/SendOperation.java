package com.example.algorithm.operations;

import com.example.model.Opinion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SendOperation implements Operation{
    @Getter
    private int fromId;
    @Getter
    private int toId;
    @Getter
    private Opinion sentOpinion;

    @Override
    public OperationType getType() {
        return OperationType.SEND;
    }
}
