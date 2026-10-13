package com.ministere.education.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:ministere_education.db";
    private static DatabaseManager instance;
    
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "SQLite JDBC Driver not found", e);
        }
    }
    
    private DatabaseManager() {
        initializeDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement()) {
            
            createTables(stmt);
            logger.info("✅ Base de données initialisée avec succès");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de l'initialisation de la base de données", e);
        }
    }
    
    private void createTables(Statement stmt) throws SQLException {
        // Table des demandes
        String createDemandesTable = 
            "CREATE TABLE IF NOT EXISTS demandes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "type_diplome TEXT NOT NULL CHECK (type_diplome IN ('CEPE', 'BEPC', 'BACC'))," +
            "nom_complet TEXT NOT NULL," +
            "numero_examen TEXT NOT NULL UNIQUE," +
            "annee_examen INTEGER NOT NULL," +
            "lieu_examen TEXT NOT NULL," +
            "cin TEXT," +
            "date_demande DATE NOT NULL DEFAULT CURRENT_DATE," +
            "date_reception DATE," +
            "date_livraison DATE," +
            "statut TEXT NOT NULL DEFAULT 'EN_COURS' CHECK (statut IN ('EN_COURS', 'RECU', 'LIVRE'))," +
            "verrouille BOOLEAN NOT NULL DEFAULT 0," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        // Table des statistiques
        String createStatsTable = 
            "CREATE TABLE IF NOT EXISTS statistiques_mensuelles (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "annee INTEGER NOT NULL," +
            "mois INTEGER NOT NULL," +
            "total_demandes INTEGER DEFAULT 0," +
            "demandes_en_cours INTEGER DEFAULT 0," +
            "demandes_recues INTEGER DEFAULT 0," +
            "demandes_livrees INTEGER DEFAULT 0," +
            "UNIQUE(annee, mois))";
        
        // Exécution des créations
        stmt.execute(createDemandesTable);
        stmt.execute(createStatsTable);
        
        // Insertion de données de test
        insertTestData(stmt);
        
        logger.info("✅ Structure de la base de données créée avec succès");
    }
    
    private void insertTestData(Statement stmt) throws SQLException {
        // Vérifier si des données existent déjà
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM demandes");
        if (rs.next() && rs.getInt(1) == 0) {
            // Insérer des données de test
            String[] testData = {
                "INSERT INTO demandes (type_diplome, nom_complet, numero_examen, annee_examen, lieu_examen, cin, statut) VALUES " +
                "('CEPE', 'JEAN DUPONT', 'CEPE2024001', 2024, 'ECOLE ANTSIRABE', '101234567890', 'EN_COURS')",
                
                "INSERT INTO demandes (type_diplome, nom_complet, numero_examen, annee_examen, lieu_examen, cin, statut) VALUES " +
                "('CEPE', 'MARIE RAKOTO', 'CEPE2024002', 2024, 'ECOLE TANA', '101234567891', 'RECU')",
                
                "INSERT INTO demandes (type_diplome, nom_complet, numero_examen, annee_examen, lieu_examen, cin, statut) VALUES " +
                "('BEPC', 'PIERRE RANDRIJAONA', 'BEPC2024001', 2024, 'LYCEE FIANARA', '101234567892', 'EN_COURS')",
                
                "INSERT INTO demandes (type_diplome, nom_complet, numero_examen, annee_examen, lieu_examen, cin, statut) VALUES " +
                "('BEPC', 'SOPHIE RAZAFY', 'BEPC2024002', 2024, 'LYCEE TOLIARA', '101234567893', 'LIVRE')"
            };
            
            for (String sql : testData) {
                stmt.execute(sql);
            }
            logger.info("✅ Données de test insérées avec succès");
        }
        rs.close();
    }
    
    public Connection getConnection() throws SQLException {
        return getNewConnection();
    }
    
    private Connection getNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // Configurer la connexion pour éviter les fermetures prématurées
        conn.setAutoCommit(true);
        return conn;
    }
    
    public boolean testConnection() {
        try (Connection conn = getNewConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Test de connexion échoué", e);
            return false;
        }
    }
}