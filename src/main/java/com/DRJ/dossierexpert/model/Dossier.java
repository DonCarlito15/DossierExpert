package com.DRJ.dossierexpert.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    private String statut; // "prêt" ou "Pas prêt" (stocké en base)
    private String remarques;
    private Long personneId;
    private String nomPersonne;
    private String prenomPersonne;
    private String dossierNombre;
    private boolean etatDossier;
    private String createdAt;
    private String updatedAt;

    // ==================== CONSTRUCTEURS ====================

    public Dossier() {
        this.statut = "Pas prêt";
        this.etatDossier = true;
        this.dateDossier = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public Dossier(String numDossier, String source, String avocat) {
        this();
        this.numDossier = numDossier;
        this.source = source;
        this.avocat = avocat;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumDossier() { return numDossier; }
    public void setNumDossier(String numDossier) { this.numDossier = numDossier; }

    public String getNumMessagerie() { return numMessagerie; }
    public void setNumMessagerie(String numMessagerie) { this.numMessagerie = numMessagerie; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getAvocat() { return avocat; }
    public void setAvocat(String avocat) { this.avocat = avocat; }

    public Double getLInteret() { return lInteret; }
    public void setLInteret(Double lInteret) { this.lInteret = lInteret; }
    public void setLInteret(String lInteret) {
        if (lInteret != null && !lInteret.isEmpty()) {
            try { this.lInteret = Double.parseDouble(lInteret); } 
            catch (NumberFormatException e) { this.lInteret = 0.0; }
        } else { this.lInteret = 0.0; }
    }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public void setMontant(String montant) {
        if (montant != null && !montant.isEmpty()) {
            try { this.montant = Double.parseDouble(montant); } 
            catch (NumberFormatException e) { this.montant = 0.0; }
        } else { this.montant = 0.0; }
    }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    // ✅ UNE SEULE méthode setDateDossier (String)
    public String getDateDossier() { return dateDossier; }
    public void setDateDossier(String dateDossier) { this.dateDossier = dateDossier; }

    // ✅ Méthode utilitaire pour LocalDate (si besoin)
    public void setDateDossierFromLocalDate(LocalDate date) {
        this.dateDossier = date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    // ✅ Retourne la date en LocalDate (pour les traitements)
    public LocalDate getDateDossierAsLocalDate() {
        if (dateDossier != null && !dateDossier.isEmpty()) {
            try {
                return LocalDate.parse(dateDossier);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String getReferencesMessagerie() { return referencesMessagerie; }
    public void setReferencesMessagerie(String referencesMessagerie) { this.referencesMessagerie = referencesMessagerie; }

    // ✅ UNE SEULE méthode getStatut
    public String getStatut() { return statut; }

    // ✅ setStatut avec conversion arabe
    public void setStatut(String statut) {
        if ("جاهز".equals(statut)) {
            this.statut = "prêt";
        } else if ("غير جاهز".equals(statut)) {
            this.statut = "Pas prêt";
        } else if (statut != null) {
            this.statut = statut;
        } else {
            this.statut = "Pas prêt";
        }
    }

    // ✅ Méthode pour afficher le statut en arabe
    public String getStatutArabe() {
        if ("prêt".equals(statut)) {
            return "جاهز";
        } else {
            return "غير جاهز";
        }
    }

    public String getStatutFrancais() {
        return "prêt".equals(statut) ? "Prêt" : "Pas prêt";
    }

    public String getStatutAvecIcone() {
        return "prêt".equals(statut) ? "✅ جاهز" : "❌ غير جاهز";
    }

    public String getRemarques() { return remarques; }
    public void setRemarques(String remarques) { this.remarques = remarques; }

    public Long getPersonneId() { return personneId; }
    public void setPersonneId(Long personneId) { this.personneId = personneId; }

    public String getNomPersonne() { return nomPersonne; }
    public void setNomPersonne(String nomPersonne) { this.nomPersonne = nomPersonne; }

    public String getPrenomPersonne() { return prenomPersonne; }
    public void setPrenomPersonne(String prenomPersonne) { this.prenomPersonne = prenomPersonne; }

    public String getPersonneComplet() {
        if (prenomPersonne != null && nomPersonne != null) {
            return prenomPersonne + " " + nomPersonne;
        }
        return "";
    }

    public String getDossierNombre() { return dossierNombre; }
    public void setDossierNombre(String dossierNombre) { this.dossierNombre = dossierNombre; }

    public boolean isEtatDossier() { return etatDossier; }
    public void setEtatDossier(boolean etatDossier) { this.etatDossier = etatDossier; }
    public void setEtatDossier(String etatDossier) {
        this.etatDossier = "نشط".equals(etatDossier) || "true".equals(etatDossier) || "1".equals(etatDossier);
    }

    public String getEtatDossierTexte() {
        return etatDossier ? "نشط" : "غير نشط";
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // ==================== MÉTHODES MÉTIER ====================

    public boolean isPret() {
        return "prêt".equals(this.statut);
    }

    public void marquerCommePret() {
        this.statut = "prêt";
    }

    public void marquerCommeNonPret() {
        this.statut = "Pas prêt";
    }

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

    public String getInteretFormate() {
        if (lInteret != null) {
            return String.format("%.2f", lInteret);
        }
        return "0.00";
    }

    public String getMontantFormate() {
        if (montant != null) {
            return String.format("%.2f", montant);
        }
        return "0.00";
    }

    public String getMontantTotalFormate() {
        return String.format("%.2f", getMontantTotal());
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
        return Objects.equals(id, dossier.id) || Objects.equals(numDossier, dossier.numDossier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numDossier);
    }
}