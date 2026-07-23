package com.DRJ.dossierexpert.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire de connexion à la base de données
 * Pattern Singleton
 *
 * @author DossierExpert
 * @version 1.0
 */
public class DatabaseConnection {

    // ==================== LOGGER ====================

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // ==================== SINGLETON ====================

    private static DatabaseConnection instance;

    private DatabaseConnection() {
        loadProperties();
        connect();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // ==================== ATTRIBUTS ====================

    private Connection connection;
    private Properties properties;

    // Paramètres de connexion par défaut
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/dossierexpert";
    private static final String DEFAULT_USERNAME = "dossierexpert_user";
    private static final String DEFAULT_PASSWORD = "Dossier@2024#Secure";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    // ==================== CONFIGURATION ====================

    /**
     * Charge les propriétés de connexion depuis le fichier de configuration
     */
    private void loadProperties() {
        properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                LOGGER.warning("Fichier db.properties non trouvé, utilisation des valeurs par défaut");
                setDefaultProperties();
            } else {
                properties.load(input);
                LOGGER.info("Propriétés de connexion chargées avec succès");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du chargement des propriétés, utilisation des valeurs par défaut", e);
            setDefaultProperties();
        }
    }

    /**
     * Définit les propriétés par défaut
     */
    private void setDefaultProperties() {
        properties.setProperty("db.url", DEFAULT_URL);
        properties.setProperty("db.username", DEFAULT_USERNAME);
        properties.setProperty("db.password", DEFAULT_PASSWORD);
        properties.setProperty("db.driver", DEFAULT_DRIVER);
        properties.setProperty("db.maxPoolSize", "10");
        properties.setProperty("db.minPoolSize", "5");
        properties.setProperty("db.connectionTimeout", "30000");
        properties.setProperty("db.idleTimeout", "600000");
        properties.setProperty("db.maxLifetime", "1800000");
    }

    // ==================== CONNEXION ====================

    /**
     * Établit la connexion à la base de données
     */
    private void connect() {
        try {
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String driver = properties.getProperty("db.driver");

            // Charger le driver JDBC
            Class.forName(driver);

            // Établir la connexion
            connection = DriverManager.getConnection(url, username, password);
            LOGGER.info("Connexion à la base de données établie avec succès");

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC non trouvé", e);
            throw new RuntimeException("Driver JDBC non trouvé: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur de connexion à la base de données", e);
            throw new RuntimeException("Erreur de connexion à la base de données: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère la connexion à la base de données
     *
     * @return La connexion active
     * @throws SQLException En cas d'erreur de connexion
     */
    public Connection getConnection() throws SQLException {
        // Vérifier si la connexion existe et est valide
        if (connection == null || connection.isClosed()) {
            LOGGER.info("Connexion fermée ou inexistante, reconnexion...");
            connect();
        }
        return connection;
    }

    /**
     * Teste la connexion à la base de données
     *
     * @return true si la connexion est valide
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Test de connexion échoué", e);
            return false;
        }
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Connexion à la base de données fermée");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Erreur lors de la fermeture de la connexion", e);
            }
        }
    }

    // ==================== GETTERS ====================

    /**
     * Récupère une propriété de connexion
     *
     * @param key La clé de la propriété
     * @return La valeur de la propriété
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Récupère l'URL de connexion
     *
     * @return L'URL de connexion
     */
    public String getUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Récupère le nom d'utilisateur
     *
     * @return Le nom d'utilisateur
     */
    public String getUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * Récupère le mot de passe
     *
     * @return Le mot de passe
     */
    public String getPassword() {
        return properties.getProperty("db.password");
    }

    /**
     * Récupère le driver JDBC
     *
     * @return Le driver JDBC
     */
    public String getDriver() {
        return properties.getProperty("db.driver");
    }

    /**
     * Vérifie si la connexion est active
     *
     * @return true si la connexion est active
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Récupère les informations de la connexion pour affichage
     *
     * @return Chaîne formatée des informations de connexion
     */
    public String getConnectionInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 Informations de connexion\n");
        sb.append("├─ URL: ").append(getUrl()).append("\n");
        sb.append("├─ Utilisateur: ").append(getUsername()).append("\n");
        sb.append("├─ Driver: ").append(getDriver()).append("\n");
        sb.append("└─ Statut: ").append(isConnected() ? "✅ Connecté" : "❌ Déconnecté");
        return sb.toString();
    }

    /**
     * Réinitialise la connexion
     */
    public void resetConnection() {
        closeConnection();
        connect();
        LOGGER.info("Connexion réinitialisée");
    }

    /**
     * Reconnecte à la base de données en cas de perte de connexion
     *
     * @return true si la reconnexion a réussi
     */
    public boolean reconnect() {
        try {
            closeConnection();
            connect();
            return isConnected();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Reconnexion échouée", e);
            return false;
        }
    }
}