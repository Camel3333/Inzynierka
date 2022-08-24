package com.example.model;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

import java.util.*;

public class MyVertex<V> implements Vertex<V>, Agent {
    private V id;
    @Getter
    private BooleanProperty isTraitor = new SimpleBooleanProperty();

    @Getter
    private BooleanProperty isSupporting;

    @Getter
    private List<BooleanProperty> knowledge = new ArrayList<>();

    public MyVertex(V id){
        isSupporting = new SimpleBooleanProperty(true);
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

    @Override
    public BooleanProperty isSupportingOpinion() {
        return isSupporting;
    }

    public void setIsTraitor(boolean isTraitor) {
        this.isTraitor.set(isTraitor);
    }

    public void setIsSupporting(boolean isSupporting) {
        this.isSupporting.set(isSupporting);
    }

    public BooleanProperty getNextOpinion(MyVertex<V> vertex){
        if(isTraitor.getValue() && (int) vertex.element() % 2 == 0){
            return new SimpleBooleanProperty(!isSupporting.getValue());
        }
        else{
            return new SimpleBooleanProperty(isSupporting.getValue());
        }
    }

    public void receiveOpinion(BooleanProperty agentOpinion){
        if(isSupporting == null){
            isSupporting = agentOpinion;
        }
        knowledge.add(agentOpinion);
    }

    public boolean getMajorityVote(){
        return knowledge.stream()
                .filter(BooleanExpression::getValue)
                .count() > knowledge.size() / 2;
    }

    public int getMajorityVoteCount(){
        return (int) knowledge.stream()
                .filter(o -> o.getValue() == getMajorityVote())
                .count();
    }

    public void chooseMajority(){
        isSupporting.set(getMajorityVote());
    }

    public void chooseMajorityWithTieBreaker(BooleanProperty kingOpinion, int condition){
        if(getMajorityVoteCount() > condition){
            isSupporting.set(getMajorityVote());
        }
        else{
            isSupporting.set(kingOpinion.getValue());
        }
    }

    public void test() {
        isSupporting.set(!isSupporting.getValue());
    }
}
