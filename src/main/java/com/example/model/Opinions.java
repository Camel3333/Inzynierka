package com.example.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

public class Opinions implements IOpinions{
    @Getter
    @Setter
    private ObservableList<IOpinion> opinions = FXCollections.observableArrayList();

    public void addOpinion(IOpinion opinion){
        if (!opinions.contains(opinion)){
            opinions.add(opinion);
        }
    }
    public void removeOpinion(IOpinion opinion){
        opinions.remove(opinion);
    }
}
