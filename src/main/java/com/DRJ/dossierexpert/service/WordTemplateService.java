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
            templateStream = getClass().getClassLoader()
                    .getResourceAsStream(templateResourcePath);

            if (templateStream == null) {
                LOGGER.log(Level.SEVERE, "❌ Template non trouvé : " + templateResourcePath);
                return false;
            }

            document = new XWPFDocument(templateStream);
            templateStream.close();

            Map<String, String> data = prepareData(dossier);
            replaceAllPlaceholders(document, data);
            replaceInHeadersAndFooters(document, data);

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

    private Map<String, String> prepareData(Dossier dossier) {
        Map<String, String> data = new HashMap<>();

        // ================================================================
        // 1. DONNÉES DU DOSSIER
        // ================================================================
        
        data.put("numDossier", getValue(dossier.getNumDossier()));
        data.put("num_dossier", getValue(dossier.getNumDossier()));
        data.put("numMessage", getValue(dossier.getNumMessagerie()));
        data.put("numMessagerie", getValue(dossier.getNumMessagerie()));
        data.put("source", getValue(dossier.getSource()));
        data.put("avocat", getValue(dossier.getAvocat()));
        data.put("interest", dossier.getLInteret() != null ? String.format("%.2f", dossier.getLInteret()) : "0.00");
        data.put("interet", dossier.getLInteret() != null ? String.format("%.2f", dossier.getLInteret()) : "0.00");
        data.put("montant", dossier.getMontant() != null ? String.format("%.2f", dossier.getMontant()) : "0.00");
        data.put("references", getValue(dossier.getReferencesMessagerie()));
        data.put("decision", getValue(dossier.getDecision()));

        // ✅ Date du dossier avec LocalDate
        if (dossier.getDateDossier() != null) {
            data.put("date", dossier.getDateDossierAsString());
            data.put("dateDossier", dossier.getDateDossierAsString());
            data.put("dateDossierFormatted", dossier.getDateDossierFormatted());
        } else {
            data.put("date", "");
            data.put("dateDossier", "");
            data.put("dateDossierFormatted", "");
        }

        data.put("statut", getValue(dossier.getStatut()));
        String statutArabe = "prêt".equals(dossier.getStatut()) ? "جاهز" : "غير جاهز";
        data.put("statutArabe", statutArabe);
        data.put("statut_arabe", statutArabe);
        data.put("etatDossier", dossier.isEtatDossier() ? "نشط" : "غير نشط");
        data.put("etat_dossier", dossier.isEtatDossier() ? "نشط" : "غير نشط");
        data.put("remarques", dossier.getRemarques() != null && !dossier.getRemarques().isEmpty()
                ? dossier.getRemarques() : "لا توجد ملاحظات");

        // ================================================================
        // 2. DATES (Plusieurs formats pour compatibilité)
        // ================================================================
        
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        String dateFormatDash = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        data.put("dateCreation", dateFormatDash);
        data.put("date_creation", dateFormatDash);
        data.put("datecreation", dateFormatDash);
        data.put("dateCreationFr", dateFormatDash);

        String dateFormatSlash = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        data.put("dateNow", dateFormatSlash);

        String dateFormatFrench = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
        data.put("dateCreationFrancais", dateFormatFrench);
        data.put("dateNowFr", dateFormatFrench);

        String dateFormatEnglish = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
        data.put("dateCreationEn", dateFormatEnglish);

        String dateFormatArabic = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ar")));
        data.put("dateCreationArabe", dateFormatArabic);

        String dateFormatIso = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        data.put("dateCreationIso", dateFormatIso);

        String dateTimeFormat = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        data.put("dateCreationHeure", dateTimeFormat);

        return data;
    }

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

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {
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

        String newText = text;
        boolean hasChanged = false;

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (newText.contains(placeholder)) {
                newText = newText.replace(placeholder, entry.getValue());
                hasChanged = true;
            }
        }

        if (hasChanged) {
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

            while (paragraph.getRuns().size() > 0) {
                paragraph.removeRun(0);
            }

            XWPFRun newRun = paragraph.createRun();
            newRun.setText(newText);

            if (containsArabic(newText)) {
                try {
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Impossible de définir l'alignement RTL", e);
                }
            }

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