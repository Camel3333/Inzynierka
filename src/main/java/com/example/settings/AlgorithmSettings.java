package com.example.settings;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmSettings {
    Map<String, AlgorithmSetting<?>> settings = new HashMap<>();

    public Map<String, AlgorithmSetting<?>> getSettings() {
        return settings;
    }
}
