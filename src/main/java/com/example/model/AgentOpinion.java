package com.example.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AgentOpinion implements Opinion {
    @Getter
    private final String name;
    @Getter
    private final BooleanProperty supports;

    public AgentOpinion(String name, boolean supports){
        this.name = name;
        this.supports = new SimpleBooleanProperty(supports);
    }

    @Override
    public BooleanProperty isSupporting() {
        return supports;
    }

    public void setIsSupporting(boolean isSupporting){
        this.supports.set(isSupporting);
    }

    public boolean compareOpinion(Opinion opinion){
        if(name.equals(opinion.getName()) && supports.getValue() == opinion.isSupporting().getValue()){
            return true;
        }
        return false;
    }

}
