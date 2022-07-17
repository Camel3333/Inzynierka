package com.example.animation;

import com.example.algorithm.AlgorithmType;

public class AnimationEngineFactory {
    public AnimationEngine create(AlgorithmType type){
        switch (type){
            case LAMPORT -> {return new LamportAnimationEngine();}
            case KING -> {return new KingAnimationEngine();}
        }
        return null;
    }
}
