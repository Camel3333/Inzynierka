package com.example.model;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public MyVertex(V id){
        this.id = id;
    }

    public void setElement(V element){
        id = element;
    }

    public void sendOpinions(MyVertex<V> vertex){
        Opinions opinions = new AgentOpinions();
        for(Opinion opinion : this.opinions.getOpinions()){
            if(isTraitor.getValue()){
                opinions.addOpinion(new AgentOpinion(opinion.getName(), !opinion.isSupporting().getValue())); //%2?
            }
            else{
                opinions.addOpinion(new AgentOpinion(opinion.getName(), opinion.isSupporting().getValue()));
            }
        }
        vertex.receiveOpinions(this, opinions);
    }

    public void receiveOpinions(MyVertex<V> vertex, Opinions agentOpinions){
        knowledge.put(vertex, (AgentOpinions) agentOpinions);
    }

    public void chooseMajority(){
        for(Opinion opinion : this.opinions.getOpinions()){
            int against = 0;
            int supports = 0;
            Iterator<Map.Entry<MyVertex<V>, AgentOpinions>> iterator = knowledge.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<MyVertex<V>, AgentOpinions> entry = iterator.next();
                if(entry.getValue().getOpinionByName(opinion.getName()) != null){ //else?
                    if(entry.getValue().getOpinionByName(opinion.getName()).isSupporting().getValue()){
                        supports += 1;
                    }
                    else{
                        against += 1;
                    }
                }
            }
            opinion.setIsSupporting(supports > against);
        }
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
}
