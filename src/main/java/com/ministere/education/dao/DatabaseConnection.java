package com.ministere.education.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:gestion_diplomes.db";
    
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(true);
            System.out.println("✅ Connexion à SQLite établie.");
            return connection;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "❌ Erreur de connexion à la base de données", ex);
            return null;
        }
    }
}