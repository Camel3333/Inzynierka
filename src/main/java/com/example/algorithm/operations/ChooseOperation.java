package com.example.algorithm.operations;

import com.example.model.Opinion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ChooseOperation implements Operation{
    @Getter
    int id;
    @Getter
    Opinion chosenOpinion;

    @Override
    public OperationType getType() {
        return OperationType.CHOOSE;
    }
}
