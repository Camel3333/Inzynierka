package com.example.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

public class AgentOpinions implements Opinions {
    @Getter
    @Setter
    private ObservableList<Opinion> opinions = FXCollections.observableArrayList();

    public void addOpinion(Opinion opinion){
        if (!opinions.contains(opinion)){
            opinions.add(opinion);
        }
    }
    public void removeOpinion(Opinion opinion){
        opinions.remove(opinion);
    }
}
