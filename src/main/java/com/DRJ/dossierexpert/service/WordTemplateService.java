package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.model.Dossier;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordTemplateService {

    private static final Logger LOGGER = Logger.getLogger(WordTemplateService.class.getName());
    private static WordTemplateService instance;

    private WordTemplateService() {}

    public static WordTemplateService getInstance() {
        if (instance == null) {
            instance = new WordTemplateService();
        }
        return instance;
    }

    public boolean generateWordFromTemplate(Dossier dossier, String outputPath) {
        return generateWordFromTemplate(dossier, "templates/template.docx", outputPath);
    }

    public boolean generateWordFromTemplate(Dossier dossier, String templateResourcePath, String outputPath) {
        if (dossier == null) {
            LOGGER.log(Level.SEVERE, "❌ Dossier est null");
            return false;
        }

        if (templateResourcePath == null || templateResourcePath.isEmpty()) {
            LOGGER.log(Level.SEVERE, "❌ Chemin du template non fourni");
            return false;
        }

        XWPFDocument document = null;
        InputStream templateStream = null;
        FileOutputStream fos = null;

        try {
            // 1. Charger le template
            templateStream = getClass().getClassLoader()
                    .getResourceAsStream(templateResourcePath);

            if (templateStream == null) {
                LOGGER.log(Level.SEVERE, "❌ Template non trouvé : " + templateResourcePath);
                return false;
            }

            document = new XWPFDocument(templateStream);
            templateStream.close();

            // 2. Préparer les données
            Map<String, String> data = prepareData(dossier);

            // 3. Remplacer les balises en conservant le style
            replaceAllPlaceholders(document, data);
            replaceInHeadersAndFooters(document, data);

            // 4. Sauvegarder
            File outputFile = new File(outputPath);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(outputFile);
            document.write(fos);
            fos.flush();

            LOGGER.info("✅ Word généré avec succès : " + outputPath);
            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur : " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (templateStream != null) templateStream.close();
                if (fos != null) fos.close();
                if (document != null) document.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Erreur fermeture ressources", e);
            }
        }
    }

    /**
     * ✅ Prépare toutes les données pour le template
     */
    private Map<String, String> prepareData(Dossier dossier) {
        Map<String, String> data = new HashMap<>();

        // ================================================================
        // 1. DONNÉES DU DOSSIER
        // ================================================================
        
        // Numéro de dossier (plusieurs formats)
        data.put("numDossier", getValue(dossier.getNumDossier()));
        data.put("num_dossier", getValue(dossier.getNumDossier()));
        
        // Numéro de messagerie
        data.put("numMessage", getValue(dossier.getNumMessagerie()));
        data.put("numMessagerie", getValue(dossier.getNumMessagerie()));
        
        // Source
        data.put("source", getValue(dossier.getSource()));
        
        // Avocat
        data.put("avocat", getValue(dossier.getAvocat()));
        
        // Intérêt
        data.put("interest", dossier.getLInteret() != null ? String.format("%.2f", dossier.getLInteret()) : "0.00");
        data.put("interet", dossier.getLInteret() != null ? String.format("%.2f", dossier.getLInteret()) : "0.00");
        
        // Montant
        data.put("montant", dossier.getMontant() != null ? String.format("%.2f", dossier.getMontant()) : "0.00");
        
        // Références
        data.put("references", getValue(dossier.getReferencesMessagerie()));
        
        // Décision
        data.put("decision", getValue(dossier.getDecision()));
        
        // Date du dossier
        data.put("date", getValue(dossier.getDateDossier()));
        data.put("dateDossier", getValue(dossier.getDateDossier()));
        
        // Statut (ENUM)
        data.put("statut", getValue(dossier.getStatut()));
        
        // Statut en arabe
        String statutArabe = "prêt".equals(dossier.getStatut()) ? "جاهز" : "غير جاهز";
        data.put("statutArabe", statutArabe);
        data.put("statut_arabe", statutArabe);
        
        // État du dossier
        data.put("etatDossier", dossier.isEtatDossier() ? "نشط" : "غير نشط");
        data.put("etat_dossier", dossier.isEtatDossier() ? "نشط" : "غير نشط");
        
        // Remarques
        data.put("remarques", dossier.getRemarques() != null && !dossier.getRemarques().isEmpty()
                ? dossier.getRemarques() : "لا توجد ملاحظات");

        // ================================================================
        // 2. DATES (Plusieurs formats pour compatibilité)
        // ================================================================
        
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        
        // Format: 05-08-2026 (date du jour demandée)
        String dateFormatDash = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        data.put("date", dateFormatDash);
        data.put("dateCreation", dateFormatDash);
        data.put("date_creation", dateFormatDash);
        data.put("datecreation", dateFormatDash);
        data.put("dateCreationFr", dateFormatDash);

        // Format: 05/08/2026
        String dateFormatSlash = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        data.put("dateNow", dateFormatSlash);

        // Format: 5 août 2026 (français)
        String dateFormatFrench = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
        data.put("dateCreationFrancais", dateFormatFrench);
        data.put("dateNowFr", dateFormatFrench);

        // Format: 05 August 2026 (anglais)
        String dateFormatEnglish = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
        data.put("dateCreationEn", dateFormatEnglish);

        // Format: 05 أغسطس 2026 (arabe)
        String dateFormatArabic = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ar")));
        data.put("dateCreationArabe", dateFormatArabic);

        // Format: 2026-08-05 (standard international)
        String dateFormatIso = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        data.put("dateCreationIso", dateFormatIso);

        // Format avec heure: 05/08/2026 14:30
        String dateTimeFormat = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        data.put("dateCreationHeure", dateTimeFormat);

        // ================================================================
        // 3. LOGS DE DÉBOGAGE
        // ================================================================
        
        LOGGER.info("📊 Données préparées :");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            LOGGER.info("   {" + entry.getKey() + "} = " + entry.getValue());
        }

        return data;
    }

    /**
     * ✅ Remplace toutes les balises dans le document
     */
    private void replaceAllPlaceholders(XWPFDocument document, Map<String, String> data) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, data);
        }

        for (XWPFTable table : document.getTables()) {
            replaceInTable(table, data);
        }
    }

    private void replaceInTable(XWPFTable table, Map<String, String> data) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    replaceInParagraph(paragraph, data);
                }
                for (XWPFTable innerTable : cell.getTables()) {
                    replaceInTable(innerTable, data);
                }
            }
        }
    }

    private void replaceInHeadersAndFooters(XWPFDocument document, Map<String, String> data) {
        try {
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph p : header.getParagraphs()) {
                    replaceInParagraph(p, data);
                }
                for (XWPFTable t : header.getTables()) {
                    replaceInTable(t, data);
                }
            }

            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph p : footer.getParagraphs()) {
                    replaceInParagraph(p, data);
                }
                for (XWPFTable t : footer.getTables()) {
                    replaceInTable(t, data);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur dans en-têtes/pieds", e);
        }
    }

    /**
     * ✅ Remplace les balises en conservant le style original
     */
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {
        // 1. Récupérer tout le texte du paragraphe
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null) {
                fullText.append(runText);
            }
        }

        String text = fullText.toString();
        if (text == null || text.isEmpty()) {
            return;
        }

        // 2. Remplacer les balises
        String newText = text;
        boolean hasChanged = false;

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (newText.contains(placeholder)) {
                newText = newText.replace(placeholder, entry.getValue());
                hasChanged = true;
                LOGGER.info("✅ Remplacé : " + placeholder + " -> " + entry.getValue());
            }
        }

        // 3. Si le texte a changé, remplacer en conservant le style
        if (hasChanged) {
            // Sauvegarder les propriétés du premier run
            XWPFRun firstRun = null;
            boolean isBold = false;
            boolean isItalic = false;
            int fontSize = 12;
            String fontFamily = "Arial";
            String color = null;
            UnderlinePatterns underline = UnderlinePatterns.NONE;

            if (paragraph.getRuns().size() > 0) {
                firstRun = paragraph.getRuns().get(0);
                isBold = firstRun.isBold();
                isItalic = firstRun.isItalic();
                if (firstRun.getFontSize() > 0) {
                    fontSize = firstRun.getFontSize();
                }
                if (firstRun.getFontFamily() != null) {
                    fontFamily = firstRun.getFontFamily();
                }
                color = firstRun.getColor();
                underline = firstRun.getUnderline();
            }

            // Supprimer tous les runs
            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }

            // Créer un nouveau run avec le même style
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(newText);

            // Si le texte contient de l'arabe, aligner à droite
            if (containsArabic(newText)) {
                try {
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Impossible de définir l'alignement RTL", e);
                }
            }

            // Réappliquer le style original
            newRun.setBold(isBold);
            newRun.setItalic(isItalic);
            newRun.setFontSize(fontSize);
            newRun.setFontFamily(fontFamily);
            if (color != null && !color.isEmpty()) {
                newRun.setColor(color);
            }
            if (underline != UnderlinePatterns.NONE) {
                newRun.setUnderline(underline);
            }
        }
    }

    /**
     * ✅ Vérifie si une chaîne contient des caractères arabes
     */
    private boolean containsArabic(String s) {
        if (s == null) return false;
        return s.codePoints().anyMatch(cp -> (cp >= 0x0600 && cp <= 0x06FF) ||
                (cp >= 0x0750 && cp <= 0x077F) ||
                (cp >= 0x08A0 && cp <= 0x08FF) ||
                (cp >= 0xFB50 && cp <= 0xFDFF) ||
                (cp >= 0xFE70 && cp <= 0xFEFF));
    }

    private String getValue(String value) {
        return value != null && !value.isEmpty() ? value : "—";
    }
}