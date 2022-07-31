package com.example.animation;

import com.example.algorithm.AlgorithmType;
import com.example.controller.GraphController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AnimationEngineFactory {
    @Autowired
    GraphController graphController;

    public AnimationEngine create(AlgorithmType type){
        switch (type){
            case LAMPORT -> {return new LamportAnimationEngine(graphController);}
            case KING -> {return new KingAnimationEngine(graphController);}
        }
        return null;
    }
}
