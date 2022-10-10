package com.example.animation;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.Semaphore;

public class AnimationRunner {

    public void runAnimationsConcurrently(List<Animation> animations) {
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(animations);
        runAnimation(parallelTransition);
    }

    public void runAnimation(Animation animation) {
        Semaphore semaphore = new Semaphore(0);

        animation.setOnFinished(e -> {
            semaphore.release();
        });

        animation.play();

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            animation.jumpTo(animation.getTotalDuration().subtract(Duration.millis(1)));
        }
    }
}
