package com.DRJ.dossierexpert.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Classe modèle représentant un dossier dans le système
 *
 * @author DossierExpert
 * @version 1.0
 */
public class Dossier {

    // ==================== ATTRIBUTS ====================

    private Long id;
    private String numDossier;
    private String numMessagerie;
    private String source;
    private String avocat;
    private Double lInteret;
    private Double montant;
    private String decision;
    private String dateDossier;
    private String referencesMessagerie;
    private String statut; // "جاهز" ou "غير جاهز"
    private String remarques;
    private Long personneId; // ID de la personne associée
    private String nomPersonne; // Nom de la personne (pour affichage)
    private String prenomPersonne; // Prénom de la personne (pour affichage)
    private String dossierNombre; // ملف عدد
    private boolean etatDossier; // true = actif, false = inactif
    private String createdAt;
    private String updatedAt;

    // ==================== CONSTRUCTEURS ====================

    /**
     * Constructeur par défaut
     */
    public Dossier() {
        this.statut = "جاهز";
        this.etatDossier = true;
        this.dateDossier = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Constructeur avec les champs obligatoires
     *
     * @param numDossier Numéro du dossier
     * @param source Source du dossier
     * @param avocat Avocat assigné
     */
    public Dossier(String numDossier, String source, String avocat) {
        this();
        this.numDossier = numDossier;
        this.source = source;
        this.avocat = avocat;
    }

    /**
     * Constructeur complet
     *
     * @param numDossier Numéro du dossier
     * @param numMessagerie Numéro de messagerie
     * @param source Source
     * @param avocat Avocat
     * @param lInteret Intérêt
     * @param montant Montant
     * @param decision Décision
     * @param dateDossier Date du dossier
     * @param referencesMessagerie Références messagerie
     * @param statut Statut
     * @param remarques Remarques
     * @param personneId ID de la personne
     */
    public Dossier(String numDossier, String numMessagerie, String source, String avocat,
                   Double lInteret, Double montant, String decision, String dateDossier,
                   String referencesMessagerie, String statut, String remarques, Long personneId) {
        this.numDossier = numDossier;
        this.numMessagerie = numMessagerie;
        this.source = source;
        this.avocat = avocat;
        this.lInteret = lInteret;
        this.montant = montant;
        this.decision = decision;
        this.dateDossier = dateDossier != null ? dateDossier : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.referencesMessagerie = referencesMessagerie;
        this.statut = statut != null ? statut : "جاهز";
        this.remarques = remarques;
        this.personneId = personneId;
        this.etatDossier = true;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumDossier() {
        return numDossier;
    }

    public void setNumDossier(String numDossier) {
        this.numDossier = numDossier;
    }

    public String getNumMessagerie() {
        return numMessagerie;
    }

    public void setNumMessagerie(String numMessagerie) {
        this.numMessagerie = numMessagerie;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAvocat() {
        return avocat;
    }

    public void setAvocat(String avocat) {
        this.avocat = avocat;
    }

    public Double getLInteret() {
        return lInteret;
    }

    public void setLInteret(Double lInteret) {
        this.lInteret = lInteret;
    }

    public void setLInteret(String lInteret) {
        if (lInteret != null && !lInteret.isEmpty()) {
            try {
                this.lInteret = Double.parseDouble(lInteret);
            } catch (NumberFormatException e) {
                this.lInteret = 0.0;
            }
        } else {
            this.lInteret = 0.0;
        }
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public void setMontant(String montant) {
        if (montant != null && !montant.isEmpty()) {
            try {
                this.montant = Double.parseDouble(montant);
            } catch (NumberFormatException e) {
                this.montant = 0.0;
            }
        } else {
            this.montant = 0.0;
        }
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getDateDossier() {
        return dateDossier;
    }

    public void setDateDossier(String dateDossier) {
        this.dateDossier = dateDossier;
    }

    public void setDateDossier(LocalDate dateDossier) {
        if (dateDossier != null) {
            this.dateDossier = dateDossier.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public String getReferencesMessagerie() {
        return referencesMessagerie;
    }

    public void setReferencesMessagerie(String referencesMessagerie) {
        this.referencesMessagerie = referencesMessagerie;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public Long getPersonneId() {
        return personneId;
    }

    public void setPersonneId(Long personneId) {
        this.personneId = personneId;
    }

    public String getNomPersonne() {
        return nomPersonne;
    }

    public void setNomPersonne(String nomPersonne) {
        this.nomPersonne = nomPersonne;
    }

    public String getPrenomPersonne() {
        return prenomPersonne;
    }

    public void setPrenomPersonne(String prenomPersonne) {
        this.prenomPersonne = prenomPersonne;
    }

    public String getPersonneComplet() {
        if (prenomPersonne != null && nomPersonne != null) {
            return prenomPersonne + " " + nomPersonne;
        }
        return "";
    }

    public String getDossierNombre() {
        return dossierNombre;
    }

    public void setDossierNombre(String dossierNombre) {
        this.dossierNombre = dossierNombre;
    }

    public boolean isEtatDossier() {
        return etatDossier;
    }

    public void setEtatDossier(boolean etatDossier) {
        this.etatDossier = etatDossier;
    }

    public void setEtatDossier(String etatDossier) {
        this.etatDossier = "نشط".equals(etatDossier) || "true".equals(etatDossier) || "1".equals(etatDossier);
    }

    public String getEtatDossierTexte() {
        return etatDossier ? "نشط" : "غير نشط";
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== MÉTHODES MÉTIER ====================

    /**
     * Vérifie si le dossier est prêt
     *
     * @return true si le statut est "جاهز"
     */
    public boolean isPret() {
        return "جاهز".equals(this.statut);
    }

    /**
     * Marque le dossier comme prêt
     */
    public void marquerCommePret() {
        this.statut = "جاهز";
    }

    /**
     * Marque le dossier comme non prêt
     */
    public void marquerCommeNonPret() {
        this.statut = "غير جاهز";
    }

    /**
     * Bascule l'état actif/inactif du dossier
     */
    public void basculerEtat() {
        this.etatDossier = !this.etatDossier;
    }

    /**
     * Active le dossier
     */
    public void activer() {
        this.etatDossier = true;
    }

    /**
     * Désactive le dossier
     */
    public void desactiver() {
        this.etatDossier = false;
    }

    /**
     * Calcule le montant total (montant + intérêt)
     *
     * @return Montant total
     */
    public Double getMontantTotal() {
        if (montant != null && lInteret != null) {
            return montant + lInteret;
        } else if (montant != null) {
            return montant;
        } else if (lInteret != null) {
            return lInteret;
        }
        return 0.0;
    }

    /**
     * Vérifie si le dossier a un montant
     *
     * @return true si le montant est supérieur à 0
     */
    public boolean hasMontant() {
        return montant != null && montant > 0;
    }

    /**
     * Vérifie si le dossier a des remarques
     *
     * @return true si les remarques ne sont pas vides
     */
    public boolean hasRemarques() {
        return remarques != null && !remarques.trim().isEmpty();
    }

    /**
     * Retourne le statut en arabe
     *
     * @return "جاهز" ou "غير جاهز"
     */
    public String getStatutArabe() {
        return statut;
    }

    /**
     * Retourne le statut en français
     *
     * @return "Prêt" ou "Pas prêt"
     */
    public String getStatutFrancais() {
        return "جاهز".equals(statut) ? "Prêt" : "Pas prêt";
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    @Override
    public String toString() {
        return "Dossier{" +
                "id=" + id +
                ", numDossier='" + numDossier + '\'' +
                ", source='" + source + '\'' +
                ", avocat='" + avocat + '\'' +
                ", montant=" + montant +
                ", statut='" + statut + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dossier dossier = (Dossier) o;
        return Objects.equals(id, dossier.id) ||
                Objects.equals(numDossier, dossier.numDossier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numDossier);
    }

    // ==================== MÉTHODES D'AFFICHAGE ====================

    /**
     * Retourne les informations du dossier pour l'affichage
     *
     * @return Chaîne formatée des informations
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("📁 ").append(numDossier).append("\n");
        sb.append("├─ المصدر: ").append(source != null ? source : "—").append("\n");
        sb.append("├─ المحامي: ").append(avocat != null ? avocat : "—").append("\n");
        sb.append("├─ المبلغ: ").append(montant != null ? String.format("%.2f", montant) : "—").append("\n");
        sb.append("└─ الحالة: ").append(statut != null ? statut : "—");
        return sb.toString();
    }

    /**
     * Retourne un tableau d'objets pour l'exportation
     *
     * @return Tableau d'objets
     */
    public Object[] toArray() {
        return new Object[]{
                numDossier,
                numMessagerie,
                source,
                avocat,
                lInteret,
                montant,
                decision,
                dateDossier,
                referencesMessagerie,
                statut,
                remarques,
                etatDossier ? "نشط" : "غير نشط"
        };
    }

    // ==================== FORMATAGE DES NOMBRES ====================

    /**
     * Retourne l'intérêt formaté
     *
     * @return Intérêt formaté avec 2 décimales
     */
    public String getInteretFormate() {
        if (lInteret != null) {
            return String.format("%.2f", lInteret);
        }
        return "0.00";
    }

    /**
     * Retourne le montant formaté
     *
     * @return Montant formaté avec 2 décimales
     */
    public String getMontantFormate() {
        if (montant != null) {
            return String.format("%.2f", montant);
        }
        return "0.00";
    }

    /**
     * Retourne le montant total formaté
     *
     * @return Montant total formaté avec 2 décimales
     */
    public String getMontantTotalFormate() {
        return String.format("%.2f", getMontantTotal());
    }

    /**
     * Retourne le statut avec une icône
     *
     * @return Statut avec icône
     */
    public String getStatutAvecIcone() {
        if (isPret()) {
            return "✅ " + statut;
        } else {
            return "❌ " + statut;
        }
    }
}
