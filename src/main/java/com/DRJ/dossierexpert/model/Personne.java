package com.DRJ.dossierexpert.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Classe modèle représentant une personne (utilisateur) dans le système
 *
 * @author DossierExpert
 * @version 1.0
 */
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

    /**
     * Constructeur par défaut
     */
    public Personne() {
        this.estActif = true;
        this.role = "UTILISATEUR";
        this.dateInscription = LocalDateTime.now();
    }

    /**
     * Constructeur avec les champs obligatoires
     *
     * @param nom Nom de la personne
     * @param prenom Prénom de la personne
     * @param email Email
     * @param motDePasse Mot de passe
     */
    public Personne(String nom, String prenom, String email, String motDePasse) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    /**
     * Constructeur complet
     *
     * @param id ID
     * @param nom Nom
     * @param prenom Prénom
     * @param email Email
     * @param motDePasse Mot de passe
     * @param telephone Téléphone
     * @param role Rôle
     * @param estActif État actif
     */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public LocalDateTime getDernierAcces() {
        return dernierAcces;
    }

    public void setDernierAcces(LocalDateTime dernierAcces) {
        this.dernierAcces = dernierAcces;
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

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Retourne le nom complet de la personne
     *
     * @return Prénom + Nom
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    /**
     * Retourne le nom complet formaté pour l'affichage
     *
     * @return Nom complet avec titre
     */
    public String getNomCompletAffiche() {
        return getNomComplet();
    }

    /**
     * Vérifie si la personne est un administrateur
     *
     * @return true si le rôle est ADMIN
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * Vérifie si la personne est un utilisateur
     *
     * @return true si le rôle est UTILISATEUR
     */
    public boolean isUtilisateur() {
        return "UTILISATEUR".equalsIgnoreCase(role);
    }

    /**
     * Vérifie si la personne est un invité
     *
     * @return true si le rôle est INVITE
     */
    public boolean isInvite() {
        return "INVITE".equalsIgnoreCase(role);
    }

    /**
     * Active la personne
     */
    public void activer() {
        this.estActif = true;
    }

    /**
     * Désactive la personne
     */
    public void desactiver() {
        this.estActif = false;
    }

    /**
     * Met à jour le dernier accès avec la date actuelle
     */
    public void updateDernierAcces() {
        this.dernierAcces = LocalDateTime.now();
    }

    /**
     * Vérifie si l'email est valide
     *
     * @return true si l'email est valide
     */
    public boolean hasValidEmail() {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    /**
     * Vérifie si le mot de passe est valide (au moins 8 caractères)
     *
     * @return true si le mot de passe est valide
     */
    public boolean hasValidPassword() {
        return motDePasse != null && motDePasse.length() >= 8;
    }

    // ==================== MÉTHODES D'AFFICHAGE ====================

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

    /**
     * Retourne les informations de la personne pour l'affichage
     *
     * @return Chaîne formatée
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("👤 ").append(getNomComplet()).append("\n");
        sb.append("├─ Email: ").append(email).append("\n");
        sb.append("├─ Rôle: ").append(role).append("\n");
        sb.append("├─ Téléphone: ").append(telephone != null ? telephone : "—").append("\n");
        sb.append("└─ Statut: ").append(estActif ? "✅ Actif" : "❌ Inactif");
        return sb.toString();
    }

    /**
     * Retourne le rôle en arabe
     *
     * @return Rôle en arabe
     */
    public String getRoleArabe() {
        switch (role) {
            case "ADMIN": return "مدير";
            case "UTILISATEUR": return "مستخدم";
            case "INVITE": return "زائر";
            default: return role;
        }
    }

    /**
     * Retourne le statut en arabe
     *
     * @return Statut en arabe
     */
    public String getStatutArabe() {
        return estActif ? "نشط" : "غير نشط";
    }

    // ==================== FORMATAGE DES DATES ====================

    /**
     * Retourne la date d'inscription formatée
     *
     * @return Date formatée
     */
    public String getDateInscriptionFormatee() {
        if (dateInscription == null) return "—";
        return dateInscription.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Retourne le dernier accès formaté
     *
     * @return Date formatée
     */
    public String getDernierAccesFormate() {
        if (dernierAcces == null) return "—";
        return dernierAcces.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // ==================== MÉTHODES D'AUTHENTIFICATION ====================

    /**
     * Vérifie l'authentification
     *
     * @param email Email à vérifier
     * @param motDePasse Mot de passe à vérifier
     * @return true si l'authentification est réussie
     */
    public boolean authenticate(String email, String motDePasse) {
        return this.email != null && this.email.equals(email) &&
                this.motDePasse != null && this.motDePasse.equals(motDePasse);
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     *
     * @param roleName Nom du rôle
     * @return true si l'utilisateur a le rôle
     */
    public boolean hasRole(String roleName) {
        return role != null && role.equalsIgnoreCase(roleName);
    }
}