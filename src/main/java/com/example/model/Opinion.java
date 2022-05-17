package com.example.model;

import javafx.beans.property.BooleanProperty;

public interface Opinion {
    String getName();
    BooleanProperty isSupporting();
    void setIsSupporting(boolean isSupporting);
    boolean compareOpinion(Opinion opinion);
}
