package com.DRJ.dossierexpert.utils;

import com.DRJ.dossierexpert.model.Personne;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Gestionnaire de sessions pour l'application
 * Pattern Singleton
 *
 * @author DossierExpert
 * @version 1.0
 */
public class SessionManager {

    // ==================== SINGLETON ====================

    private static SessionManager instance;

    private SessionManager() {
        // Constructeur privé pour le Singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ==================== ATTRIBUTS ====================

    private Personne currentPersonne;
    private String sessionToken;
    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionExpiryTime;
    private boolean isActive = false;
    private static final int SESSION_TIMEOUT_MINUTES = 30; // Durée de session en minutes

    // ==================== GESTION DE SESSION ====================

    /**
     * Crée une nouvelle session pour un utilisateur
     *
     * @param personnel L'utilisateur connecté
     * @return true si la session a été créée avec succès
     */
    public boolean createSession(Personne personnel) {
        if (personnel == null) {
            return false;
        }

        this.currentPersonne = personnel;
        this.sessionToken = generateToken();
        this.sessionStartTime = LocalDateTime.now();
        this.sessionExpiryTime = this.sessionStartTime.plusMinutes(SESSION_TIMEOUT_MINUTES);
        this.isActive = true;

        return true;
    }

    /**
     * Vérifie s'il y a une session active
     *
     * @return true si une session est active et valide
     */
    public boolean hasActiveSession() {
        if (!isActive || currentPersonne == null) {
            return false;
        }

        // Vérifier si la session a expiré
        if (isSessionExpired()) {
            destroySession();
            return false;
        }

        return true;
    }

    /**
     * Vérifie si la session a expiré
     *
     * @return true si la session a expiré
     */
    public boolean isSessionExpired() {
        if (sessionExpiryTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(sessionExpiryTime);
    }

    /**
     * Prolonge la session
     */
    public void refreshSession() {
        if (isActive && currentPersonne != null) {
            this.sessionExpiryTime = LocalDateTime.now().plusMinutes(SESSION_TIMEOUT_MINUTES);
        }
    }

    /**
     * Détruit la session actuelle
     */
    public void destroySession() {
        this.currentPersonne = null;
        this.sessionToken = null;
        this.sessionStartTime = null;
        this.sessionExpiryTime = null;
        this.isActive = false;
    }

    // ==================== GETTERS ====================

    /**
     * Retourne l'utilisateur actuellement connecté
     *
     * @return L'utilisateur connecté ou null
     */
    public Personne getCurrentPersonne() {
        if (hasActiveSession()) {
            return currentPersonne;
        }
        return null;
    }

    /**
     * Retourne le token de session
     *
     * @return Le token de session
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Retourne l'heure de début de session
     *
     * @return Heure de début
     */
    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    /**
     * Retourne l'heure d'expiration de la session
     *
     * @return Heure d'expiration
     */
    public LocalDateTime getSessionExpiryTime() {
        return sessionExpiryTime;
    }

    /**
     * Vérifie si la session est active
     *
     * @return true si la session est active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Retourne le temps restant avant expiration (en minutes)
     *
     * @return Temps restant en minutes
     */
    public long getRemainingTimeMinutes() {
        if (sessionExpiryTime == null) {
            return 0;
        }
        long minutes = java.time.Duration.between(LocalDateTime.now(), sessionExpiryTime).toMinutes();
        return Math.max(0, minutes);
    }

    /**
     * Retourne le temps restant avant expiration (en secondes)
     *
     * @return Temps restant en secondes
     */
    public long getRemainingTimeSeconds() {
        if (sessionExpiryTime == null) {
            return 0;
        }
        long seconds = java.time.Duration.between(LocalDateTime.now(), sessionExpiryTime).getSeconds();
        return Math.max(0, seconds);
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Génère un token de session unique
     *
     * @return Token généré
     */
    private String generateToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    /**
     * Retourne les informations de la session pour affichage
     *
     * @return Chaîne formatée des informations de session
     */
    public String getSessionInfo() {
        if (!hasActiveSession()) {
            return "Aucune session active";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 Informations de session\n");
        sb.append("├─ Utilisateur: ").append(currentPersonne.getPrenom()).append(" ").append(currentPersonne.getNom()).append("\n");
        sb.append("├─ Email: ").append(currentPersonne.getEmail()).append("\n");
        sb.append("├─ Token: ").append(sessionToken != null ? sessionToken.substring(0, 20) + "..." : "N/A").append("\n");
        sb.append("├─ Début: ").append(sessionStartTime != null ? sessionStartTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "N/A").append("\n");
        sb.append("├─ Expiration: ").append(sessionExpiryTime != null ? sessionExpiryTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "N/A").append("\n");
        sb.append("└─ Temps restant: ").append(getRemainingTimeMinutes()).append(" minutes");

        return sb.toString();
    }

    // ==================== VALIDATION ====================

    /**
     * Valide un token de session
     *
     * @param token Token à valider
     * @return true si le token est valide
     */
    public boolean validateToken(String token) {
        if (!isActive || sessionToken == null) {
            return false;
        }
        return sessionToken.equals(token) && !isSessionExpired();
    }

    /**
     * Vérifie si l'utilisateur connecté a un rôle donné
     *
     * @param role Le rôle à vérifier
     * @return true si l'utilisateur a le rôle
     */
    public boolean hasRole(String role) {
        if (!hasActiveSession() || currentPersonne == null) {
            return false;
        }
        return role.equalsIgnoreCase(currentPersonne.getRole());
    }

    /**
     * Vérifie si l'utilisateur connecté est un administrateur
     *
     * @return true si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}