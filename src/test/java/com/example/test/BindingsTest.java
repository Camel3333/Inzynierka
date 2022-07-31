package com.example.test;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.Test;

public class BindingsTest {
    @Test
    public void bindAndTest(){
        BooleanProperty op1 = new SimpleBooleanProperty(false);
        BooleanProperty op2 = new SimpleBooleanProperty(false);
        BooleanProperty res = new SimpleBooleanProperty(false);
        res.bind(Bindings.and(op1,op2));
        res.addListener(e -> System.out.println("Value changed"));
    }
}
