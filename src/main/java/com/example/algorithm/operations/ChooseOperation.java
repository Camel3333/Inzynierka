package com.example.algorithm.operations;

import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ChooseOperation implements Operation{
    @Getter
    int id;
    @Getter
    BooleanProperty chosenOpinion;

    @Override
    public OperationType getType() {
        return OperationType.CHOOSE;
    }
}
