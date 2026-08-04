package com.DRJ.dossierexpert.service;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordPrintService {

    private static final Logger LOGGER = Logger.getLogger(WordPrintService.class.getName());

    /**
     * ✅ Ouvre le fichier Word
     */
    public boolean openWordFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.log(Level.SEVERE, "❌ Fichier non trouvé : " + filePath);
                return false;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                LOGGER.info("✅ Fichier Word ouvert : " + filePath);
                return true;
            } else {
                LOGGER.severe("❌ Desktop non supporté");
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'ouverture du fichier : " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * ✅ Imprime le fichier Word
     */
    public boolean printWordFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.log(Level.SEVERE, "❌ Fichier non trouvé : " + filePath);
                return false;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().print(file);
                LOGGER.info("✅ Impression en cours : " + filePath);
                return true;
            } else {
                LOGGER.severe("❌ Desktop non supporté");
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors de l'impression : " + e.getMessage(), e);
            return false;
        }
    }
}