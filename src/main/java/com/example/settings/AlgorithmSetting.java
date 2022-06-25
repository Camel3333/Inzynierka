package com.example.settings;

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
    public Boolean isProperValue(T value) {
        return validateArgument.apply(valueType.cast(value));
    }

    @Override
    public void setValue(T value) {
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
