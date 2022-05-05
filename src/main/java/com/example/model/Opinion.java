package com.example.model;

import javafx.beans.property.BooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Opinion implements IOpinion{
    @Getter
    private final String name;
    @Getter
    private final BooleanProperty supports;

    @Override
    public BooleanProperty isSupporting() {
        return supports;
    }
}
