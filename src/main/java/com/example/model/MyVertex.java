package com.example.model;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.util.*;

public class MyVertex<V> implements Vertex<V>, Agent {
    private V id;
    @Getter
    private final BooleanProperty isTraitor = new SimpleBooleanProperty();

    @Getter
    private BooleanProperty isSupporting;

    @Getter
    private List<Boolean> knowledge = new ArrayList<>();

    @Getter
    private final StringProperty knowledgeInfo = new SimpleStringProperty("");

    public MyVertex(V id){
        isSupporting = new SimpleBooleanProperty(true);
        this.id = id;
        updateKnowledgeInfo();
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

    public void updateKnowledgeInfo() {
        int attackCount = (int) knowledge.stream().filter(e -> e).count();
        int retreatAttack = knowledge.size() - attackCount;
        if (knowledge.size() == 0) {
            knowledgeInfo.set("attack: -\nretreat: -");
        }
        else {
            knowledgeInfo.set("attack: " + attackCount + "\nretreat: " + retreatAttack);
        }
    }

    public void clearKnowledge() {
        knowledge = new ArrayList<>();
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
        knowledge.add(agentOpinion.getValue());
    }

    public boolean getMajorityVote(){
        return knowledge.stream()
                .filter(e -> e)
                .count() > knowledge.size() / 2;
    }

    public int getMajorityVoteCount(){
        return (int) knowledge.stream()
                .filter(o -> o == getMajorityVote())
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

    @Override
    public String toString() {
        return "MyVertex{" +
                "id=" + id +
                ", isTraitor=" + isTraitor +
                ", forAttack=" + isSupporting +
                ", knowledge=" + knowledge +
                '}';
    }
}
