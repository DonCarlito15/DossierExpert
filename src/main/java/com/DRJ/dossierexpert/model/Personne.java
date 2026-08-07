package com.DRJ.dossierexpert.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Personne {

    // ==================== ATTRIBUTS ====================

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String role; // ADMIN, UTILISATEUR, INVITE
    private boolean estActif;
    private LocalDateTime dateInscription;
    private LocalDateTime dernierAcces;
    private String createdAt;
    private String updatedAt;

    // ==================== CONSTRUCTEURS ====================

    public Personne() {
        this.estActif = true;
        this.role = "UTILISATEUR";
        this.dateInscription = LocalDateTime.now();
    }

    public Personne(String nom, String prenom, String email, String motDePasse) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public Personne(Long id, String nom, String prenom, String email, String motDePasse,
                    String telephone, String role, boolean estActif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.role = role != null ? role : "UTILISATEUR";
        this.estActif = estActif;
        this.dateInscription = LocalDateTime.now();
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEstActif() { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }

    public LocalDateTime getDernierAcces() { return dernierAcces; }
    public void setDernierAcces(LocalDateTime dernierAcces) { this.dernierAcces = dernierAcces; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // ==================== MÉTHODES UTILITAIRES ====================

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public String getNomCompletAffiche() {
        return getNomComplet();
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isUtilisateur() {
        return "UTILISATEUR".equalsIgnoreCase(role);
    }

    public boolean isInvite() {
        return "INVITE".equalsIgnoreCase(role);
    }

    public void activer() {
        this.estActif = true;
    }

    public void desactiver() {
        this.estActif = false;
    }

    public void updateDernierAcces() {
        this.dernierAcces = LocalDateTime.now();
    }

    public boolean hasValidEmail() {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public boolean hasValidPassword() {
        return motDePasse != null && motDePasse.length() >= 8;
    }

    public String getRoleArabe() {
        switch (role) {
            case "ADMIN": return "مدير";
            case "UTILISATEUR": return "مستخدم";
            case "INVITE": return "زائر";
            default: return role;
        }
    }

    public String getStatutArabe() {
        return estActif ? "نشط" : "غير نشط";
    }

    public String getDateInscriptionFormatee() {
        if (dateInscription == null) return "—";
        return dateInscription.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDernierAccesFormate() {
        if (dernierAcces == null) return "—";
        return dernierAcces.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public boolean authenticate(String email, String motDePasse) {
        return this.email != null && this.email.equals(email) &&
                this.motDePasse != null && this.motDePasse.equals(motDePasse);
    }

    public boolean hasRole(String roleName) {
        return role != null && role.equalsIgnoreCase(roleName);
    }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", estActif=" + estActif +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return Objects.equals(id, personne.id) ||
                Objects.equals(email, personne.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}