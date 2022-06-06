package com.example.model;

import javafx.collections.ObservableList;

public interface Opinions {
    void addOpinion(Opinion opinion);
    void removeOpinion(Opinion opinion);
    ObservableList<Opinion> getOpinions();
    Opinion getOpinionByName(String name);
    boolean compareOpinions(Opinions opinions);
}
