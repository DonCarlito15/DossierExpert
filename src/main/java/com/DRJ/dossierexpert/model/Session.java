package com.DRJ.dossierexpert.model;

import java.time.LocalDateTime;

public class Session {

    private Long id;
    private String token;
    private Long personneId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExpiration;
    private String adresseIp;
    private String userAgent;
    private boolean estActive;

    // ==================== CONSTRUCTEURS ====================

    public Session() {
        this.estActive = true;
        this.dateCreation = LocalDateTime.now();
    }

    public Session(String token, Long personneId) {
        this();
        this.token = token;
        this.personneId = personneId;
        this.dateExpiration = LocalDateTime.now().plusMinutes(30);
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getPersonneId() { return personneId; }
    public void setPersonneId(Long personneId) { this.personneId = personneId; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public String getAdresseIp() { return adresseIp; }
    public void setAdresseIp(String adresseIp) { this.adresseIp = adresseIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public boolean isEstActive() { return estActive; }
    public void setEstActive(boolean estActive) { this.estActive = estActive; }

    // ==================== MÉTHODES UTILITAIRES ====================

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(dateExpiration);
    }

    public void refresh() {
        this.dateExpiration = LocalDateTime.now().plusMinutes(30);
    }

    public boolean isValid() {
        return estActive && !isExpired();
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", personneId=" + personneId +
                ", estActive=" + estActive +
                '}';
    }
}