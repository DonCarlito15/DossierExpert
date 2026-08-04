package com.DRJ.dossierexpert.utils;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {

    /**
     * Animation de fondu (apparition)
     */
    public static void fadeIn(Node node, double durationMillis) {
        FadeTransition ft = new FadeTransition(Duration.millis(durationMillis), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /**
     * Animation de fondu (disparition)
     */
    public static void fadeOut(Node node, double durationMillis) {
        FadeTransition ft = new FadeTransition(Duration.millis(durationMillis), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    /**
     * Animation de glissement
     */
    public static void slideIn(Node node, double durationMillis, double fromX, double toX) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMillis), node);
        tt.setFromX(fromX);
        tt.setToX(toX);
        tt.play();
    }

    /**
     * Animation combinée (fondu + glissement)
     */
    public static void fadeAndSlideIn(Node node, double durationMillis) {
        node.setOpacity(0);
        node.setTranslateX(50);

        FadeTransition ft = new FadeTransition(Duration.millis(durationMillis), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMillis), node);
        tt.setFromX(50);
        tt.setToX(0);

        ft.play();
        tt.play();
    }
}