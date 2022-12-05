package com.example.settings;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerSettingTextFieldTest {
    private JFXPanel panel = new JFXPanel(); //allows to run test with JavaFX objects
    private IntegerSettingTextField settingNode;

    @BeforeEach
    void setUp() {
        settingNode = new IntegerSettingTextField();

        AlgorithmSetting<Integer> relatedSetting = new AlgorithmSetting<>(
                "depth",
                1,
                Integer.class,
                input -> input >= 0 && input < 20);

        settingNode.setContainedSetting(relatedSetting);
    }

    @Test
    public void initialStateTest(){
        assertTrue(settingNode.getIsValidProperty().get());
        assertEquals(settingNode.getContainedSetting().get().getValue().toString(), settingNode.getText());
    }

    @Test
    public void changeToValidInputTest(){
        //When
        settingNode.textProperty().setValue("5");

        //Then
        assertTrue(settingNode.getIsValidProperty().get());
        assertEquals(5, settingNode.getContainedSetting().get().getValue());
    }

    @Test
    public void changeToInvalidInputTypeTest(){
        //When
        settingNode.textProperty().setValue("abc");

        //Then
        assertFalse(settingNode.getIsValidProperty().get());
        assertEquals(1, settingNode.getContainedSetting().get().getValue());
    }

    @Test
    public void changeToInvalidInputTest(){
        //When
        settingNode.textProperty().setValue("-10");

        //Then
        assertFalse(settingNode.getIsValidProperty().get());
        assertEquals(1, settingNode.getContainedSetting().get().getValue());
    }
}