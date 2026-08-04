package com.DRJ.dossierexpert.service;

import com.DRJ.dossierexpert.model.Dossier;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImprimanteService {

    private static ImprimanteService instance;
    private static final Color BLEU_FONCE = new DeviceRgb(44, 62, 80);
    private static final Color BLEU_CLAIR = new DeviceRgb(41, 81, 125);
    private static final Color VERT = new DeviceRgb(39, 174, 96);
    private static final Color ROUGE = new DeviceRgb(231, 76, 60);
    private static final Color GRIS_FOND = new DeviceRgb(248, 249, 250);
    private static final Color GRIS_TEXTE = new DeviceRgb(189, 195, 199);

    private PdfFont arabicFont;

    private ImprimanteService() {
        try {
            // Essayer de charger une police qui supporte l'arabe
            // Option 1: police incluse dans les ressources (ex: resources/fonts/NotoSansArabic-Regular.ttf)
            try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("NotoSansArabic-Regular.ttf")) {
                if (is != null) {
                    arabicFont = PdfFontFactory.createFont(is, com.itextpdf.io.font.PdfEncodings.IDENTITY_H, true);
                    System.out.println("✅ Police arabe chargée depuis les ressources (stream)");
                } else {
                    throw new RuntimeException("Ressource police introuvable");
                }
            } catch (Exception e) {
                // Option 2: police système Windows (Tahoma ou Arial Unicode)
                try {
                    String[] sysFonts = new String[]{
                            "C:/Windows/Fonts/tahoma.ttf",
                            "C:/Windows/Fonts/arialuni.ttf",
                            "C:/Windows/Fonts/arial.ttf",
                            "C:/Windows/Fonts/seguiemj.ttf"
                    };
                    boolean loaded = false;
                    for (String f : sysFonts) {
                        try {
                            File ff = new File(f);
                            if (ff.exists()) {
                                arabicFont = PdfFontFactory.createFont(f, com.itextpdf.io.font.PdfEncodings.IDENTITY_H, true);
                                System.out.println("✅ Police chargée depuis le système: " + f);
                                loaded = true;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                    if (!loaded) throw new RuntimeException("Aucune police système trouvée");
                } catch (Exception ex) {
                    // Option 3: fallback to a standard font (may not support Arabic)
                    try {
                        arabicFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
                        System.out.println("✅ Police Helvetica chargée (fallback)");
                    } catch (Exception exc) {
                        System.err.println("❌ Aucune police trouvée, utilisation par défaut");
                        arabicFont = null;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la police: " + e.getMessage());
            arabicFont = null;
        }
    }

    public static ImprimanteService getInstance() {
        if (instance == null) {
            instance = new ImprimanteService();
        }
        return instance;
    }

    /**
     * ✅ Génère un PDF avec support arabe
     */
    public boolean generatePDF(Dossier dossier, String filePath) {
        if (dossier == null) {
            System.err.println("❌ Dossier est null");
            return false;
        }

        try {
            // Créer le dossier parent si nécessaire
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(40, 40, 40, 40);

            // ================================================================
            // EN-TÊTE : Logo + Titre
            // ================================================================

            Paragraph logo = createParagraph("خبير الملفات")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(BLEU_FONCE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(0);
            document.add(logo);

            Paragraph subtitle = createParagraph("نظام إدارة الملفات القانونية")
                    .setFontSize(11)
                    .setFontColor(BLEU_CLAIR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(0);
            document.add(subtitle);

            Paragraph reportTitle = createParagraph("تقرير الملف - نموذج رسمي")
                    .setFontSize(10)
                    .setFontColor(GRIS_TEXTE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(reportTitle);

            // Ligne de séparation
            document.add(new Paragraph(" ").setBorderBottom(Border.NO_BORDER));

            // ================================================================
            // TITRE : معلومات الملف
            // ================================================================

            Paragraph title = createParagraph("معلومات الملف")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(BLEU_FONCE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
                    .setMarginBottom(15);
            document.add(title);

            // ================================================================
            // TABLEAU DES INFORMATIONS
            // ================================================================

            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginTop(5);
            table.setMarginBottom(15);

            // Ajouter toutes les lignes
            addRow(table, "ملف م.ق رقم :", getValue(dossier.getNumDossier()));
            addRow(table, "رقم المراسلة :", getValue(dossier.getNumMessagerie()));
            addRow(table, "المصدر :", getValue(dossier.getSource()));
            addRow(table, "المحامي :", getValue(dossier.getAvocat()));
            addRow(table, "الفائدة :", dossier.getLInteret() != null ? String.format("%.2f", dossier.getLInteret()) : "0.00");
            addRow(table, "المبلغ :", dossier.getMontant() != null ? String.format("%.2f", dossier.getMontant()) : "0.00");
            addRow(table, "ملف عدد :", getValue(dossier.getReferencesMessagerie()));
            addRow(table, "حكم او قرار عدد :", getValue(dossier.getDecision()));
            addRow(table, "التاريخ المقرر :", getValue(dossier.getDateDossier()));
            addRow(table, "مراجع المراسلة :", getValue(dossier.getStatut()));

            // Statut avec couleur
            String statutArabe = "prêt".equals(dossier.getStatut()) ? "جاهز" : "غير جاهز";
            Color statutCouleur = "prêt".equals(dossier.getStatut()) ? VERT : ROUGE;
            addRowWithColor(table, "حالة المراسلة :", statutArabe, statutCouleur);

            addRow(table, "حالة الملف :", dossier.isEtatDossier() ? "نشط" : "غير نشط");

            document.add(table);

            // ================================================================
            // REMARQUES
            // ================================================================

            Paragraph remarquesTitle = createParagraph("الملاحظات :")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(BLEU_CLAIR)
                    .setMarginBottom(5);
            document.add(remarquesTitle);

            String remarquesText = dossier.getRemarques() != null && !dossier.getRemarques().isEmpty()
                    ? dossier.getRemarques()
                    : "لا توجد ملاحظات";

            Table remarquesTable = new Table(UnitValue.createPercentArray(new float[]{100}));
            remarquesTable.setWidth(UnitValue.createPercentValue(100));

            Cell remarquesCell = new Cell()
                    .add(createParagraph(remarquesText)
                            .setFontSize(12)
                            .setFontColor(BLEU_FONCE)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBackgroundColor(GRIS_FOND)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(12);

            remarquesTable.addCell(remarquesCell);
            document.add(remarquesTable);

            // ================================================================
            // PIED DE PAGE
            // ================================================================

            document.add(new Paragraph(" ").setBorderBottom(Border.NO_BORDER));

            Paragraph footer = createParagraph("تم إنشاء هذا التقرير بواسطة نظام خبير الملفات")
                    .setFontSize(9)
                    .setFontColor(GRIS_TEXTE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(15);
            document.add(footer);

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Paragraph footerDate = createParagraph("تاريخ الإنشاء : " + date)
                    .setFontSize(9)
                    .setFontColor(GRIS_TEXTE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(2);
            document.add(footerDate);

            document.close();
            System.out.println("✅ PDF généré avec succès : " + filePath);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crée un Paragraph avec la police arabe si disponible
     */
    private Paragraph createParagraph(String text) {
        Paragraph p = new Paragraph(text);
        if (arabicFont != null) {
            p.setFont(arabicFont);
        }
        if (containsArabic(text)) {
            p.setTextAlignment(TextAlignment.RIGHT);
            p.setBaseDirection(com.itextpdf.layout.property.BaseDirection.RIGHT_TO_LEFT);
        }
        return p;
    }

    private boolean containsArabic(String s) {
        if (s == null) return false;
        return s.codePoints().anyMatch(cp -> (cp >= 0x0600 && cp <= 0x06FF) || (cp >= 0x0750 && cp <= 0x077F) || (cp >= 0x08A0 && cp <= 0x08FF) || (cp >= 0xFB50 && cp <= 0xFDFF) || (cp >= 0xFE70 && cp <= 0xFEFF));
    }

    /**
     * Retourne la valeur ou "—" si null
     */
    private String getValue(String value) {
        return value != null && !value.isEmpty() ? value : "—";
    }

    /**
     * Ajoute une ligne au tableau
     */
    private void addRow(Table table, String label, String value) {
        // Cellule label
        Paragraph labelP = createParagraph(label).setBold().setFontColor(BLEU_CLAIR);
        if (containsArabic(label)) {
            labelP.setTextAlignment(TextAlignment.RIGHT);
            labelP.setBaseDirection(com.itextpdf.layout.property.BaseDirection.RIGHT_TO_LEFT);
        }
        Cell labelCell = new Cell()
                .add(labelP)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(containsArabic(label) ? TextAlignment.RIGHT : TextAlignment.LEFT)
                .setPadding(5);

        // Cellule valeur
        Paragraph valueP = createParagraph(value).setFontColor(BLEU_FONCE);
        if (containsArabic(value)) {
            valueP.setTextAlignment(TextAlignment.RIGHT);
            valueP.setBaseDirection(com.itextpdf.layout.property.BaseDirection.RIGHT_TO_LEFT);
        }
        Cell valueCell = new Cell()
                .add(valueP)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(containsArabic(value) ? TextAlignment.RIGHT : TextAlignment.LEFT)
                .setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    /**
     * Ajoute une ligne avec couleur personnalisée pour le statut
     */
    private void addRowWithColor(Table table, String label, String value, Color color) {
        // Cellule label
        Paragraph labelP = createParagraph(label).setBold().setFontColor(BLEU_CLAIR);
        if (containsArabic(label)) {
            labelP.setTextAlignment(TextAlignment.RIGHT);
            labelP.setBaseDirection(com.itextpdf.layout.property.BaseDirection.RIGHT_TO_LEFT);
        }
        Cell labelCell = new Cell()
                .add(labelP)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(containsArabic(label) ? TextAlignment.RIGHT : TextAlignment.LEFT)
                .setPadding(5);

        // Cellule valeur avec couleur
        Paragraph valueP = createParagraph(value).setBold().setFontColor(color);
        if (containsArabic(value)) {
            valueP.setTextAlignment(TextAlignment.RIGHT);
            valueP.setBaseDirection(com.itextpdf.layout.property.BaseDirection.RIGHT_TO_LEFT);
        }
        Cell valueCell = new Cell()
                .add(valueP)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(containsArabic(value) ? TextAlignment.RIGHT : TextAlignment.LEFT)
                .setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}