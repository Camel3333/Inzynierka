package com.example.test;

import com.example.algorithm.AlgorithmSetting;
import com.example.algorithm.Setting;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class SettingTest {
    private void checkInstanceof(Object a){
        int b = (int) a;
        System.out.println(a instanceof Integer);
    }

    @Test
    public void containSettingsTest(){
        Map<String, Setting<?>> settingMap = new HashMap<>();
        settingMap.put("depth", new AlgorithmSetting<>("depth", 1, Integer.class,(value) -> value >= 0));
        var cls = settingMap.get("depth").getContainedClass();
        int a = 10;
        if (cls.isInstance(a))
            settingMap.get("depth").setValue(a);
        System.out.println(settingMap.get("depth").getValue());
    }
}
