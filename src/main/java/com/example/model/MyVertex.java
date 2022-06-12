package com.example.model;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class MyVertex<V> implements Vertex<V>, Agent {
    private V id;
    @Getter
    private BooleanProperty isTraitor = new SimpleBooleanProperty();
    @Getter
    @Setter
    private BooleanProperty supportsOpinion = new SimpleBooleanProperty();
    @Getter
    @Setter
    private AgentOpinion opinion;
    @Getter
    private List<AgentOpinion> knowledge = new ArrayList<>();

    public MyVertex(V id){
        this.id = id;
    }

    public void setElement(V element){
        id = element;
    }

    @Override
    public V element() {
        return id;
    }

    @Override
    public BooleanProperty isTraitor() {
        return isTraitor;
    }

    public void setIsTraitor(boolean isTraitor) {
        this.isTraitor = new SimpleBooleanProperty(isTraitor);
    }

    public AgentOpinion getNextOpinion(MyVertex<V> vertex){
        if(isTraitor.getValue() && (int) vertex.element() % 2 == 0){
            return new AgentOpinion(opinion.getName(), !opinion.isSupporting().getValue());
        }
        else{
            return new AgentOpinion(opinion.getName(), opinion.isSupporting().getValue());
        }
    }

    public void receiveOpinion(AgentOpinion agentOpinion){
        if(opinion == null){ // ?
            opinion = agentOpinion;
        }
        knowledge.add(agentOpinion);
    }

    public boolean getMajorityVote(){
        return knowledge.stream()
                .filter(o -> o.isSupporting().getValue())
                .count() > knowledge.size() / 2;
    }

    public int getMajorityVoteCount(){
        return (int) knowledge.stream()
                .filter(o -> o.isSupporting().getValue() == getMajorityVote())
                .count();
    }

    public void chooseMajority(){
        opinion.setIsSupporting(getMajorityVote());
    }

    public void chooseMajorityWithTieBreaker(AgentOpinion kingOpinion, int condition){
        if(getMajorityVoteCount() > condition){
            opinion.setIsSupporting(getMajorityVote());
        }
        else{
            opinion.setIsSupporting(kingOpinion.isSupporting().getValue());
        }
    }

}
