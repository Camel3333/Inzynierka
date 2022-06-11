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
    private AgentOpinions opinions;
    private Map<MyVertex<V>, AgentOpinions> knowledge = new HashMap<>();
    private List<Opinions> receivedOpinions = new ArrayList<>();

    public MyVertex(V id){
        this.id = id;
    }

    public void setElement(V element){
        id = element;
    }

    public void sendOpinions(MyVertex<V> vertex){
        Opinions opinions = new AgentOpinions();
        for(Opinion opinion : this.opinions.getOpinions()){
            if(isTraitor.getValue() && (int) element() % 2 == 0){
                opinions.addOpinion(new AgentOpinion(opinion.getName(), !opinion.isSupporting().getValue()));
            }
            else{
                opinions.addOpinion(new AgentOpinion(opinion.getName(), opinion.isSupporting().getValue()));
            }
        }
        vertex.receiveOpinions(this, opinions);
    }

    public void receiveOpinions(MyVertex<V> vertex, Opinions agentOpinions){
        if(opinions == null){
            opinions = (AgentOpinions) agentOpinions;
        }
        receivedOpinions.add(agentOpinions);
        System.out.println("#" + element() + " received " + agentOpinions.getOpinions().get(0).isSupporting().getValue() + " from #" + vertex.element());
    }

    public boolean getMajorityVote(Opinion opinion){
        return receivedOpinions.stream()
                .filter(o -> o.getOpinionByName(opinion.getName()).isSupporting().getValue())
                .count() > receivedOpinions.size() / 2;
    }

    public int getMajorityVotesCount(Opinion opinion){
        return (int) receivedOpinions.stream()
                .filter(o -> o.getOpinionByName(opinion.getName()).isSupporting().getValue() == getMajorityVote(opinion))
                .count();
    }

    public void decide(Opinions kingOpinions, int condition){ //ale king opinion moze byc zieniona jak traitor
        for(Opinion opinion : this.opinions.getOpinions()){
            if(getMajorityVotesCount(opinion) > condition){
                opinion.setIsSupporting(getMajorityVote(opinion));
            }
            else{
                opinion.setIsSupporting(kingOpinions.getOpinionByName(opinion.getName()).isSupporting().getValue());
            }
        }
    }

    public void chooseMajority(){ //change
        for(Opinion opinion : this.opinions.getOpinions()){
            /*long supports = receivedOpinions.stream()
                    .filter(o -> o.getOpinionByName(opinion.getName()).isSupporting().getValue())
                    .count();
            opinion.setIsSupporting(supports > receivedOpinions.size() - supports);*/
            opinion.setIsSupporting(getMajorityVote(opinion));
        }
        System.out.println("#" + element() + " decision " + opinions.getOpinions().get(0).isSupporting().getValue());
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
    public void setIsTraitor(boolean isTraitor) {
        this.isTraitor.setValue(isTraitor);
    }

    public void setOpinions(AgentOpinions opinions) {
        this.opinions = opinions;
    }

    public Map<MyVertex<V>, AgentOpinions> getKnowledge() {
        return knowledge;
    }

    public List<Opinions> getReceivedOpinions() {
        return receivedOpinions;
    }
}
