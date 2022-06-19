package com.example.algorithm;

import lombok.Setter;

import java.util.function.Function;

public class AlgorithmSetting<T> implements Setting<T>{
    private final String name;
    private final Function<T, Boolean> validateArgument;
    private final Class<T> valueType;
    private final T defaultValue;
    @Setter
    private String additionalErrorMessage = "";
    private T value;

    public AlgorithmSetting(String name, T defaultValue, Class<T> valueType, Function<T, Boolean> validateArgument) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
        this.validateArgument = validateArgument;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean isProperValue(Object value) {
        if (valueType.isInstance(value))
            return validateArgument.apply(valueType.cast(value));
        return false;
    }

    @Override
    public void setValue(Object value) {
        if (isProperValue(value))
            this.value = valueType.cast(value);
        else
            throw new IllegalArgumentException("Given value doesn't match setting requirements. "+additionalErrorMessage);
    }

    @Override
    public Class<T> getContainedClass() {
        return valueType;
    }

    @Override
    public T getValue(){
        return value == null ? defaultValue : value;
    }
}
