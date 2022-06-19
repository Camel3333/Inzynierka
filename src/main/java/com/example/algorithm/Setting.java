package com.example.algorithm;

public interface Setting<T> {
    String getName();
    Boolean isProperValue(Object value);
    void setValue(Object value);
    Class<T> getContainedClass();
    T getValue();
}
