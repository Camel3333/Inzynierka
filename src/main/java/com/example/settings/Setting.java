package com.example.settings;

public interface Setting<T> {
    String getName();
    Boolean isProperValue(T value);
    void setValue(T value);
    Class<T> getContainedClass();
    T getValue();
}
