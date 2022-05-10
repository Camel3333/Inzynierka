package com.example.model;

import javafx.beans.property.BooleanProperty;

public interface Agent {
    BooleanProperty isTraitor();
    void setIsTraitor(boolean isTraitor);
    Opinions getOpinions();
}
