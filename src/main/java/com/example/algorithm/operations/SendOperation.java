package com.example.algorithm.operations;

import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SendOperation implements Operation{
    @Getter
    private int fromId;
    @Getter
    private int toId;
    @Getter
    private BooleanProperty sentOpinion;

    @Override
    public OperationType getType() {
        return OperationType.SEND;
    }
}
