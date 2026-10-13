package com.ministere.education.utils;

import com.ministere.education.model.StatistiqueMensuelle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataExporter {
    
    public static void exporterDonneesMois(StatistiqueMensuelle stat, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les statistiques de " + stat.getMoisNom() + " " + stat.getAnnee());
        
        String defaultFileName = String.format("statistiques_%s_%d.csv", 
            stat.getMoisNom().toLowerCase(), stat.getAnnee());
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            
            @Override
            public String getDescription() {
                return "Fichiers CSV (*.csv)";
            }
        });
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                // Ajouter BOM pour Excel
                writer.write('\uFEFF');
                
                writer.write("Période,Total Demandes,En Cours,Reçues,Livrées,Taux Livraison,Taux Réception\n");
                
                double tauxLivraison = stat.getTotalDemandes() > 0 ? 
                    (double) stat.getDemandesLivrees() / stat.getTotalDemandes() * 100 : 0;
                double tauxReception = stat.getTotalDemandes() > 0 ? 
                    (double) stat.getDemandesRecues() / stat.getTotalDemandes() * 100 : 0;
                
                writer.write(String.format("%s %d,%d,%d,%d,%d,%.1f%%,%.1f%%\n",
                    stat.getMoisNom(), stat.getAnnee(),
                    stat.getTotalDemandes(), stat.getDemandesEnCours(),
                    stat.getDemandesRecues(), stat.getDemandesLivrees(),
                    tauxLivraison, tauxReception
                ));
                
                JOptionPane.showMessageDialog(parent,
                    "✅ Données exportées avec succès!\n\n" +
                    "Fichier: " + file.getName() + "\n" +
                    "Emplacement: " + file.getParent(),
                    "Exportation Réussie",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent,
                    "❌ Erreur lors de l'exportation:\n" + ex.getMessage(),
                    "Erreur d'Exportation",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void exporterToutesDonnees(List<StatistiqueMensuelle> stats, Component parent) {
        if (stats.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                "Aucune donnée disponible pour l'exportation.",
                "Exportation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter toutes les données statistiques");
        
        String defaultFileName = String.format("statistiques_completes_%s.csv", 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            
            @Override
            public String getDescription() {
                return "Fichiers CSV (*.csv)";
            }
        });
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                // Ajouter BOM pour Excel
                writer.write('\uFEFF');
                
                writer.write("Période,Total Demandes,En Cours,Reçues,Livrées,Taux Livraison,Taux Réception\n");
                
                for (StatistiqueMensuelle stat : stats) {
                    double tauxLivraison = stat.getTotalDemandes() > 0 ? 
                        (double) stat.getDemandesLivrees() / stat.getTotalDemandes() * 100 : 0;
                    double tauxReception = stat.getTotalDemandes() > 0 ? 
                        (double) stat.getDemandesRecues() / stat.getTotalDemandes() * 100 : 0;
                    
                    writer.write(String.format("%s %d,%d,%d,%d,%d,%.1f%%,%.1f%%\n",
                        stat.getMoisNom(), stat.getAnnee(),
                        stat.getTotalDemandes(), stat.getDemandesEnCours(),
                        stat.getDemandesRecues(), stat.getDemandesLivrees(),
                        tauxLivraison, tauxReception
                    ));
                }
                
                JOptionPane.showMessageDialog(parent,
                    "✅ Toutes les données exportées avec succès!\n\n" +
                    "Fichier: " + file.getName() + "\n" +
                    "Emplacement: " + file.getParent() + "\n" +
                    "Lignes exportées: " + stats.size(),
                    "Exportation Complète Réussie",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent,
                    "❌ Erreur lors de l'exportation:\n" + ex.getMessage(),
                    "Erreur d'Exportation",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void exporterParPeriode(List<StatistiqueMensuelle> stats, Component parent) {
        // Créer une boîte de dialogue personnalisée pour la sélection de période
        JDialog periodeDialog = new JDialog((java.awt.Frame) null, "Sélection de la période", true);
        periodeDialog.setLayout(new BorderLayout());
        periodeDialog.setSize(400, 200);
        periodeDialog.setLocationRelativeTo(parent);
        periodeDialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel titreLabel = new JLabel("Sélectionnez la période à exporter:");
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titreLabel.setForeground(new Color(33, 37, 41));
        
        JComboBox<String> anneeDebutCombo = createModernComboBox(new String[]{"2024", "2023", "2022", "2021"});
        JComboBox<String> moisDebutCombo = createModernComboBox(new String[]{"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                                                                          "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"});
        JComboBox<String> anneeFinCombo = createModernComboBox(new String[]{"2024", "2023", "2022", "2021"});
        JComboBox<String> moisFinCombo = createModernComboBox(new String[]{"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                                                                        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"});
        
        contentPanel.add(new JLabel("Année début:"));
        contentPanel.add(anneeDebutCombo);
        contentPanel.add(new JLabel("Mois début:"));
        contentPanel.add(moisDebutCombo);
        contentPanel.add(new JLabel("Année fin:"));
        contentPanel.add(anneeFinCombo);
        contentPanel.add(new JLabel("Mois fin:"));
        contentPanel.add(moisFinCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton exporterBtn = createModernGradientButton("📤 Exporter la période", new Color(67, 97, 238), 12);
        JButton annulerBtn = createModernButton("Annuler", new Color(108, 117, 125), 12);
        
        exporterBtn.addActionListener(e -> {
            periodeDialog.dispose();
            executerExportationPeriode(stats, 
                (String) anneeDebutCombo.getSelectedItem(),
                (String) moisDebutCombo.getSelectedItem(),
                (String) anneeFinCombo.getSelectedItem(),
                (String) moisFinCombo.getSelectedItem(),
                parent);
        });
        
        annulerBtn.addActionListener(e -> periodeDialog.dispose());
        
        buttonPanel.add(annulerBtn);
        buttonPanel.add(exporterBtn);
        
        periodeDialog.add(titreLabel, BorderLayout.NORTH);
        periodeDialog.add(contentPanel, BorderLayout.CENTER);
        periodeDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        periodeDialog.setVisible(true);
    }
    
    private static void executerExportationPeriode(List<StatistiqueMensuelle> toutesStats, String anneeDebut, String moisDebut, String anneeFin, String moisFin, Component parent) {
        List<StatistiqueMensuelle> statsFiltrees = new java.util.ArrayList<>();
        
        // Convertir les noms de mois en numéros
        Map<String, Integer> moisMap = new HashMap<>();
        String[] moisNoms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                             "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        for (int i = 0; i < moisNoms.length; i++) {
            moisMap.put(moisNoms[i], i + 1);
        }
        
        int debutMois = moisMap.get(moisDebut);
        int finMois = moisMap.get(moisFin);
        int debutAnnee = Integer.parseInt(anneeDebut);
        int finAnnee = Integer.parseInt(anneeFin);
        
        // Filtrer les statistiques
        for (StatistiqueMensuelle stat : toutesStats) {
            int anneeStat = stat.getAnnee();
            int moisStat = getNumeroMois(stat.getMoisNom());
            
            // Vérifier si la statistique est dans la plage sélectionnée
            if ((anneeStat > debutAnnee || (anneeStat == debutAnnee && moisStat >= debutMois)) &&
                (anneeStat < finAnnee || (anneeStat == finAnnee && moisStat <= finMois))) {
                statsFiltrees.add(stat);
            }
        }
        
        if (statsFiltrees.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                "Aucune donnée disponible pour la période sélectionnée.",
                "Période Vide",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Exporter les données filtrées
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter données de " + moisDebut + " " + anneeDebut + " à " + moisFin + " " + anneeFin);
        
        String defaultFileName = String.format("statistiques_%s%d_%s%d.csv", 
            moisDebut.toLowerCase(), debutAnnee, moisFin.toLowerCase(), finAnnee);
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                // Ajouter BOM pour Excel
                writer.write('\uFEFF');
                
                writer.write("Période,Total Demandes,En Cours,Reçues,Livrées,Taux Livraison,Taux Réception\n");
                
                for (StatistiqueMensuelle stat : statsFiltrees) {
                    double tauxLivraison = stat.getTotalDemandes() > 0 ? 
                        (double) stat.getDemandesLivrees() / stat.getTotalDemandes() * 100 : 0;
                    double tauxReception = stat.getTotalDemandes() > 0 ? 
                        (double) stat.getDemandesRecues() / stat.getTotalDemandes() * 100 : 0;
                    
                    writer.write(String.format("%s %d,%d,%d,%d,%d,%.1f%%,%.1f%%\n",
                        stat.getMoisNom(), stat.getAnnee(),
                        stat.getTotalDemandes(), stat.getDemandesEnCours(),
                        stat.getDemandesRecues(), stat.getDemandesLivrees(),
                        tauxLivraison, tauxReception
                    ));
                }
                
                JOptionPane.showMessageDialog(parent,
                    "✅ Données de période exportées avec succès!\n\n" +
                    "Période: " + moisDebut + " " + anneeDebut + " à " + moisFin + " " + anneeFin + "\n" +
                    "Fichier: " + file.getName() + "\n" +
                    "Lignes exportées: " + statsFiltrees.size(),
                    "Exportation de Période Réussie",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent,
                    "❌ Erreur lors de l'exportation:\n" + ex.getMessage(),
                    "Erreur d'Exportation",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void genererRapportComplet(List<StatistiqueMensuelle> stats, Component parent) {
        if (stats.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                "Aucune donnée disponible pour générer un rapport.",
                "Données Manquantes",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calculer les statistiques globales
        int totalDemandes = 0;
        int totalEnCours = 0;
        int totalLivrees = 0;
        int totalRecues = 0;
        
        for (StatistiqueMensuelle stat : stats) {
            totalDemandes += stat.getTotalDemandes();
            totalEnCours += stat.getDemandesEnCours();
            totalLivrees += stat.getDemandesLivrees();
            totalRecues += stat.getDemandesRecues();
        }
        
        double tauxLivraisonGlobal = totalDemandes > 0 ? (double) totalLivrees / totalDemandes * 100 : 0;
        double tauxReceptionGlobal = totalDemandes > 0 ? (double) totalRecues / totalDemandes * 100 : 0;
        
        // Afficher le rapport détaillé
        String rapport = String.format(
            "📈 RAPPORT COMPLET DES STATISTIQUES\n\n" +
            "📊 SYNTHÈSE GLOBALE:\n" +
            "• Total des demandes: %,d\n" +
            "• Demandes en cours: %,d\n" +
            "• Demandes reçues: %,d\n" +
            "• Demandes livrées: %,d\n" +
            "• Taux de livraison: %.1f%%\n" +
            "• Taux de réception: %.1f%%\n\n" +
            "📅 PÉRIODE COUVERTE:\n" +
            "• Du: %s %d\n" +
            "• Au: %s %d\n" +
            "• Nombre de mois: %d\n\n" +
            "🏆 PERFORMANCE GLOBALE: %s\n\n" +
            "Cette analyse couvre l'ensemble des données disponibles et fournit \n" +
            "une vision complète de l'activité du service des diplômes.",
            totalDemandes, totalEnCours, totalRecues, totalLivrees,
            tauxLivraisonGlobal, tauxReceptionGlobal,
            stats.get(stats.size()-1).getMoisNom(), stats.get(stats.size()-1).getAnnee(),
            stats.get(0).getMoisNom(), stats.get(0).getAnnee(),
            stats.size(),
            tauxLivraisonGlobal >= 80 ? "✅ EXCELLENTE" : tauxLivraisonGlobal >= 60 ? "⚠️ MOYENNE" : "❌ À AMÉLIORER"
        );
        
        JOptionPane.showMessageDialog(parent, rapport, 
            "Rapport Complet - " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Méthode utilitaire pour obtenir le numéro du mois
    private static int getNumeroMois(String nomMois) {
        String[] nomsMois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        for (int i = 0; i < nomsMois.length; i++) {
            if (nomsMois[i].equalsIgnoreCase(nomMois)) {
                return i + 1;
            }
        }
        return -1;
    }
    
    // Méthodes utilitaires pour les composants
    private static JButton createModernGradientButton(String text, Color backgroundColor, int fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, backgroundColor, 0, getHeight(), darkenColor(backgroundColor, 0.1f));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(255, 255, 255, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private static JButton createModernButton(String text, Color backgroundColor, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkenColor(backgroundColor, 0.1f), 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(brightenColor(backgroundColor, 0.1f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private static JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(232, 236, 241), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        combo.setMaximumSize(new Dimension(180, 36));
        return combo;
    }
    
    private static Color darkenColor(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * (1 - factor)), 0),
            Math.max((int)(color.getGreen() * (1 - factor)), 0),
            Math.max((int)(color.getBlue() * (1 - factor)), 0)
        );
    }
    
    private static Color brightenColor(Color color, float factor) {
        return new Color(
            Math.min((int)(color.getRed() * (1 + factor)), 255),
            Math.min((int)(color.getGreen() * (1 + factor)), 255),
            Math.min((int)(color.getBlue() * (1 + factor)), 255)
        );
    }
}