package com.example.algorithm;

import lombok.Setter;

import java.util.function.Function;

public class AlgorithmSetting implements Setting{
    private final String name;
    private final Function<Object, Boolean> validateArgument;
    @Setter
    private String additionalErrorMessage = "";
    private Object value;
    private Object defaultValue;

    public AlgorithmSetting(String name, Object defaultValue, Function<Object, Boolean> validateArgument) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.validateArgument = validateArgument;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean isProperValue(Object value) {
        return validateArgument.apply(value);
    }

    @Override
    public void setValue(Object value) {
        if (isProperValue(value))
            this.value = value;
        else
            throw new IllegalArgumentException("Given value doesn't match setting requirements. "+additionalErrorMessage);
    }

    @Override
    public Object getValue(){
        return value == null ? defaultValue : value;
    }
}
