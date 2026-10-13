package com.ministere.education.dao;

import com.ministere.education.database.DatabaseManager;
import com.ministere.education.model.Demande;
import com.ministere.education.model.StatistiqueMensuelle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DemandeDAO {
    private static final Logger logger = Logger.getLogger(DemandeDAO.class.getName());
    
    public boolean saveDemande(Demande demande) {
        String sql = "INSERT INTO demandes (type_diplome, nom_complet, numero_examen, annee_examen, lieu_examen, cin, date_demande, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, demande.getTypeDiplome());
            pstmt.setString(2, demande.getNomComplet());
            pstmt.setString(3, demande.getNumeroExamen());
            pstmt.setInt(4, demande.getAnneeExamen());
            pstmt.setString(5, demande.getLieuExamen());
            pstmt.setString(6, demande.getCin());
            pstmt.setDate(7, new java.sql.Date(demande.getDateDemande().getTime()));
            pstmt.setString(8, demande.getStatut());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        demande.setId(generatedKeys.getLong(1));
                    }
                }
                logger.info("✅ Demande sauvegardée: " + demande.getNomComplet());
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur sauvegarde demande: " + demande.getNomComplet(), e);
        }
        return false;
    }
    
    public List<Demande> getDemandesByType(String typeDiplome) {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes WHERE type_diplome = ? ORDER BY date_demande DESC, created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, typeDiplome);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
            
            logger.info("✅ " + demandes.size() + " demandes trouvées pour " + typeDiplome);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur récupération demandes: " + typeDiplome, e);
        }
        return demandes;
    }
    
    public boolean updateDemande(Demande demande) {
        String sql = "UPDATE demandes SET statut = ?, date_reception = ?, date_livraison = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, demande.getStatut());
            
            if (demande.getDateReception() != null) {
                pstmt.setDate(2, new java.sql.Date(demande.getDateReception().getTime()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            
            if (demande.getDateLivraison() != null) {
                pstmt.setDate(3, new java.sql.Date(demande.getDateLivraison().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            
            pstmt.setLong(4, demande.getId());
            
            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("✅ Demande mise à jour: " + demande.getNomComplet());
            }
            
            return success;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur mise à jour demande: " + demande.getId(), e);
        }
        return false;
    }
    
    public boolean deleteDemande(Long id) {
        String sql = "DELETE FROM demandes WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("✅ Demande supprimée: " + id);
            }
            
            return success;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur suppression demande: " + id, e);
        }
        return false;
    }
    
    public boolean isNumeroExamenExists(String numeroExamen) {
        String sql = "SELECT COUNT(*) FROM demandes WHERE numero_examen = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, numeroExamen);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur vérification numéro examen: " + numeroExamen, e);
        }
        return false;
    }
    
    public boolean isCinExists(String cin) {
        String sql = "SELECT COUNT(*) FROM demandes WHERE cin = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cin);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur vérification CIN: " + cin, e);
        }
        return false;
    }
    
    public Demande getDemandeById(Long id) {
        String sql = "SELECT * FROM demandes WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDemande(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur récupération demande par ID: " + id, e);
        }
        return null;
    }
    
    public List<StatistiqueMensuelle> getStatistiquesMensuelles() {
        List<StatistiqueMensuelle> statistiques = new ArrayList<>();
        
        try {
            // Méthode simple et compatible SQLite : statistiques globales
            String sql = "SELECT " +
                        "COUNT(*) as total_demandes, " +
                        "SUM(CASE WHEN statut = 'EN_COURS' THEN 1 ELSE 0 END) as demandes_en_cours, " +
                        "SUM(CASE WHEN statut = 'RECU' THEN 1 ELSE 0 END) as demandes_recues, " +
                        "SUM(CASE WHEN statut = 'LIVRE' THEN 1 ELSE 0 END) as demandes_livrees " +
                        "FROM demandes";
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                if (rs.next()) {
                    StatistiqueMensuelle stat = new StatistiqueMensuelle();
                    // Utiliser l'année et le mois actuels
                    Calendar cal = Calendar.getInstance();
                    stat.setAnnee(cal.get(Calendar.YEAR));
                    stat.setMois(cal.get(Calendar.MONTH) + 1);
                    stat.setTotalDemandes(rs.getInt("total_demandes"));
                    stat.setDemandesEnCours(rs.getInt("demandes_en_cours"));
                    stat.setDemandesRecues(rs.getInt("demandes_recues"));
                    stat.setDemandesLivrees(rs.getInt("demandes_livrees"));
                    
                    statistiques.add(stat);
                }
            }
            
            logger.info("✅ Statistiques chargées: " + statistiques.size() + " période(s)");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur lors du chargement des statistiques", e);
            
            // Créer des statistiques vides en cas d'erreur
            StatistiqueMensuelle stat = new StatistiqueMensuelle();
            Calendar cal = Calendar.getInstance();
            stat.setAnnee(cal.get(Calendar.YEAR));
            stat.setMois(cal.get(Calendar.MONTH) + 1);
            statistiques.add(stat);
        }
        
        return statistiques;
    }
    
    // Nouvelle méthode pour obtenir toutes les demandes (utilisée dans l'alternative)
    public List<Demande> getAllDemandes() {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demandes ORDER BY date_demande DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                demandes.add(mapResultSetToDemande(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Erreur récupération toutes demandes", e);
        }
        return demandes;
    }
    
    private Demande mapResultSetToDemande(ResultSet rs) throws SQLException {
        Demande demande = new Demande();
        demande.setId(rs.getLong("id"));
        demande.setTypeDiplome(rs.getString("type_diplome"));
        demande.setNomComplet(rs.getString("nom_complet"));
        demande.setNumeroExamen(rs.getString("numero_examen"));
        demande.setAnneeExamen(rs.getInt("annee_examen"));
        demande.setLieuExamen(rs.getString("lieu_examen"));
        demande.setCin(rs.getString("cin"));
        demande.setStatut(rs.getString("statut"));
        demande.setVerrouille(rs.getBoolean("verrouille"));
        demande.setCreatedAt(rs.getTimestamp("created_at"));
        demande.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // CORRECTION : Gestion robuste des dates avec parsing manuel
        try {
            Date dateDemande = rs.getDate("date_demande");
            demande.setDateDemande(dateDemande);
        } catch (SQLException e) {
            logger.warning("❌ Erreur parsing date_demande, tentative de récupération en String...");
            try {
                String dateStr = rs.getString("date_demande");
                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    // Essayer différents formats de date
                    if (dateStr.contains("-")) {
                        LocalDate localDate = LocalDate.parse(dateStr);
                        demande.setDateDemande(java.sql.Date.valueOf(localDate));
                    } else if (dateStr.contains("/")) {
                        // Format dd/MM/yyyy
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            LocalDate localDate = LocalDate.of(
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[1]), 
                                Integer.parseInt(parts[0])
                            );
                            demande.setDateDemande(java.sql.Date.valueOf(localDate));
                        }
                    }
                } else {
                    demande.setDateDemande(null);
                }
            } catch (Exception ex) {
                logger.warning("❌ Impossible de parser date_demande: " + ex.getMessage());
                demande.setDateDemande(null);
            }
        }
        
        try {
            Date dateReception = rs.getDate("date_reception");
            demande.setDateReception(dateReception);
        } catch (SQLException e) {
            logger.warning("❌ Erreur parsing date_reception, tentative de récupération en String...");
            try {
                String dateStr = rs.getString("date_reception");
                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    if (dateStr.contains("-")) {
                        LocalDate localDate = LocalDate.parse(dateStr);
                        demande.setDateReception(java.sql.Date.valueOf(localDate));
                    } else if (dateStr.contains("/")) {
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            LocalDate localDate = LocalDate.of(
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[1]), 
                                Integer.parseInt(parts[0])
                            );
                            demande.setDateReception(java.sql.Date.valueOf(localDate));
                        }
                    }
                } else {
                    demande.setDateReception(null);
                }
            } catch (Exception ex) {
                logger.warning("❌ Impossible de parser date_reception: " + ex.getMessage());
                demande.setDateReception(null);
            }
        }
        
        try {
            Date dateLivraison = rs.getDate("date_livraison");
            demande.setDateLivraison(dateLivraison);
        } catch (SQLException e) {
            logger.warning("❌ Erreur parsing date_livraison, tentative de récupération en String...");
            try {
                String dateStr = rs.getString("date_livraison");
                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    if (dateStr.contains("-")) {
                        LocalDate localDate = LocalDate.parse(dateStr);
                        demande.setDateLivraison(java.sql.Date.valueOf(localDate));
                    } else if (dateStr.contains("/")) {
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            LocalDate localDate = LocalDate.of(
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[1]), 
                                Integer.parseInt(parts[0])
                            );
                            demande.setDateLivraison(java.sql.Date.valueOf(localDate));
                        }
                    }
                } else {
                    demande.setDateLivraison(null);
                }
            } catch (Exception ex) {
                logger.warning("❌ Impossible de parser date_livraison: " + ex.getMessage());
                demande.setDateLivraison(null);
            }
        }
        
        return demande;
    }
    
    // Version alternative plus simple de mapResultSetToDemande (si vous préférez)
    private Demande mapResultSetToDemandeAlternative(ResultSet rs) throws SQLException {
        Demande demande = new Demande();
        demande.setId(rs.getLong("id"));
        demande.setTypeDiplome(rs.getString("type_diplome"));
        demande.setNomComplet(rs.getString("nom_complet"));
        demande.setNumeroExamen(rs.getString("numero_examen"));
        demande.setAnneeExamen(rs.getInt("annee_examen"));
        demande.setLieuExamen(rs.getString("lieu_examen"));
        demande.setCin(rs.getString("cin"));
        demande.setStatut(rs.getString("statut"));
        demande.setVerrouille(rs.getBoolean("verrouille"));
        demande.setCreatedAt(rs.getTimestamp("created_at"));
        demande.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Approche simple : ignorer les erreurs de date
        try {
            demande.setDateDemande(rs.getDate("date_demande"));
        } catch (Exception e) {
            demande.setDateDemande(null);
        }
        
        try {
            demande.setDateReception(rs.getDate("date_reception"));
        } catch (Exception e) {
            demande.setDateReception(null);
        }
        
        try {
            demande.setDateLivraison(rs.getDate("date_livraison"));
        } catch (Exception e) {
            demande.setDateLivraison(null);
        }
        
        return demande;
    }
    
    private Connection getConnection() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }
    
    public boolean testConnection() {
        return DatabaseManager.getInstance().testConnection();
    }
}