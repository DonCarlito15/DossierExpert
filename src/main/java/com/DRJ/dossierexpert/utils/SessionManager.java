package com.DRJ.dossierexpert.utils;

import com.DRJ.dossierexpert.model.Personne;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionManager {

    private static SessionManager instance;
    private Personne currentPersonne;
    private String sessionToken;
    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionExpiryTime;
    private boolean isActive = false;
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean createSession(Personne personne) {
        if (personne == null) {
            return false;
        }
        this.currentPersonne = personne;
        this.sessionToken = generateToken();
        this.sessionStartTime = LocalDateTime.now();
        this.sessionExpiryTime = this.sessionStartTime.plusMinutes(SESSION_TIMEOUT_MINUTES);
        this.isActive = true;
        return true;
    }

    public boolean hasActiveSession() {
        if (!isActive || currentPersonne == null) {
            return false;
        }
        if (isSessionExpired()) {
            destroySession();
            return false;
        }
        return true;
    }

    public boolean isSessionExpired() {
        if (sessionExpiryTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(sessionExpiryTime);
    }

    public void refreshSession() {
        if (isActive && currentPersonne != null) {
            this.sessionExpiryTime = LocalDateTime.now().plusMinutes(SESSION_TIMEOUT_MINUTES);
        }
    }

    public void destroySession() {
        this.currentPersonne = null;
        this.sessionToken = null;
        this.sessionStartTime = null;
        this.sessionExpiryTime = null;
        this.isActive = false;
    }

    public Personne getCurrentPersonne() {
        if (hasActiveSession()) {
            return currentPersonne;
        }
        return null;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public LocalDateTime getSessionExpiryTime() {
        return sessionExpiryTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getRemainingTimeMinutes() {
        if (sessionExpiryTime == null) {
            return 0;
        }
        long minutes = java.time.Duration.between(LocalDateTime.now(), sessionExpiryTime).toMinutes();
        return Math.max(0, minutes);
    }

    private String generateToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    public boolean validateToken(String token) {
        if (!isActive || sessionToken == null) {
            return false;
        }
        return sessionToken.equals(token) && !isSessionExpired();
    }

    public boolean hasRole(String role) {
        if (!hasActiveSession() || currentPersonne == null) {
            return false;
        }
        return role.equalsIgnoreCase(currentPersonne.getRole());
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}