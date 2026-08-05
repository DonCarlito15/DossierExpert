package com.DRJ.dossierexpert.utils;

import javafx.scene.Scene;

import java.util.List;

/**
 * Simple theme manager to apply light/dark CSS files to a Scene.
 */
public class ThemeManager {

    private static final String LIGHT = "/com/DRJ/dossierexpert/views/css/style-fixed.css";
    private static final String DARK = "/com/DRJ/dossierexpert/views/css/style-dark.css";

    public enum Theme { LIGHT_THEME, DARK_THEME }

    public static void applyTheme(Scene scene, Theme theme) {
        if (scene == null) return;
        List<String> sheets = scene.getStylesheets();
        sheets.clear();
        try {
            sheets.add(ThemeManager.class.getResource(LIGHT).toExternalForm());
            if (theme == Theme.DARK_THEME) {
                sheets.add(ThemeManager.class.getResource(DARK).toExternalForm());
            }
        } catch (Exception e) {
            // fallback: ignore if resources missing
            System.err.println("⚠️ ThemeManager: stylesheet non trouvée: " + e.getMessage());
        }
    }

    public static void toggleTheme(Scene scene) {
        if (scene == null) return;
        List<String> sheets = scene.getStylesheets();
        boolean darkPresent = sheets.stream().anyMatch(s -> s.contains("style-dark.css"));
        applyTheme(scene, darkPresent ? Theme.LIGHT_THEME : Theme.DARK_THEME);
    }
}
