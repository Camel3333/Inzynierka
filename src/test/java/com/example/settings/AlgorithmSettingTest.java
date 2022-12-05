package com.example.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmSettingTest {

    @Test
    public void integerSettingValidationTest(){
        //Given
        AlgorithmSetting<Integer> integerSetting = new AlgorithmSetting<>(
            "SimpleSetting",
            1,
            Integer.class,
            input -> input >= 0 && input < 20);

        var tooSmallValue = -10;
        var smallestPossible = 0;
        var biggestPossible = 19;
        var tooBigValue = 20;

        //Then
        assertFalse(integerSetting.isProperValue(tooSmallValue));
        assertTrue(integerSetting.isProperValue(smallestPossible));
        assertTrue(integerSetting.isProperValue(biggestPossible));
        assertFalse(integerSetting.isProperValue(tooBigValue));
    }

    @Test
    public void integerSettingInvalidSetTest(){
        //Given
        AlgorithmSetting<Integer> integerSetting = new AlgorithmSetting<>(
                "SimpleSetting",
                1,
                Integer.class,
                input -> input >= 0 && input < 20);

        var tooSmallValue = -10;

        //Then
        assertThrows(IllegalArgumentException.class, ()->integerSetting.setValue(tooSmallValue));
    }
}
