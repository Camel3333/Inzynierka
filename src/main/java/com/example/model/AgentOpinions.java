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
        if (!opinions.contains(opinion) && getOpinionByName(opinion.getName()) == null){
            opinions.add(opinion);
        }
    }
    public void removeOpinion(Opinion opinion){
        opinions.remove(opinion);
    }

    public ObservableList<Opinion> getOpinions() {
        return opinions;
    }

    public Opinion getOpinionByName(String name){
        for(Opinion opinion : this.opinions){
            if(opinion.getName().equals(name)){
                return opinion;
            }
        }
        return null;
    }

    public boolean compareOpinions(Opinions opinions){
        if(this.opinions.size() != opinions.getOpinions().size()){
            return false;
        }
        for(Opinion opinion : this.opinions){
            if(!opinion.compareOpinion(opinions.getOpinionByName(opinion.getName()))){
                return false;
            }
        }
        return true;
    }
}
