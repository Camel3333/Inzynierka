package com.example.algorithm;

public interface Setting {
    String getName();
    Boolean isProperValue(Object value);
    void setValue(Object value);
    Object getValue();
}
