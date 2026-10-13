package com.ministere.education.ui;

import com.ministere.education.dao.DemandeDAO;
import com.ministere.education.model.StatistiqueMensuelle;
import com.ministere.education.utils.DataExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private DemandeDAO demandeDAO;
    private JPanel contentPanel;
    private JLabel totalStatsLabel;
    private JComboBox<String> filtreAnneeCombo;
    private JComboBox<String> filtreMoisCombo;
    
    // Palette de couleurs moderne
    private final Color PRIMARY_COLOR = new Color(67, 97, 238);
    private final Color SECONDARY_COLOR = new Color(42, 157, 244);
    private final Color ACCENT_COLOR = new Color(56, 193, 114);
    private final Color WARNING_COLOR = new Color(255, 159, 28);
    private final Color DANGER_COLOR = new Color(235, 87, 87);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color BORDER_COLOR = new Color(232, 236, 241);
    
    public DashboardPanel() {
        this.demandeDAO = new DemandeDAO();
        setBackground(BACKGROUND_COLOR);
        initComponents();
        chargerStatistiques();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header moderne avec gradient
        JPanel headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel de filtres moderne
        JPanel filterPanel = createModernFilterPanel();
        add(filterPanel, BorderLayout.CENTER);
        
        // Panel principal avec ombres et effets modernes
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        // Personnalisation de la scrollbar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(20);
        verticalScrollBar.setBackground(BACKGROUND_COLOR);
        verticalScrollBar.setBorder(BorderFactory.createEmptyBorder());
        
        // Panel de contrôle moderne
        JPanel controlPanel = createModernControlPanel();
        
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createModernHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(67, 97, 238), 0, getHeight(), new Color(86, 61, 234));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(getWidth(), 140));
        header.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        JLabel titleLabel = new JLabel("📊 TABLEAU DE BORD ANALYTIQUE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JLabel subtitleLabel = new JLabel("Statistiques mensuelles des demandes de diplômes - Ministère de l'Éducation");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        header.add(textPanel, BorderLayout.WEST);
        
        JLabel iconLabel = new JLabel("📈");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(new Color(255, 255, 255, 180));
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        header.add(iconLabel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createModernFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(8, 0.1f),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel filtreLabel = new JLabel("🔍 FILTRES AVANCÉS");
        filtreLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filtreLabel.setForeground(TEXT_PRIMARY);
        
        filtreAnneeCombo = createModernComboBox(new String[]{"Toutes les années", "2024", "2023", "2022"});
        filtreMoisCombo = createModernComboBox(new String[]{"Tous les mois", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                                                          "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"});
        
        JButton appliquerFiltreBtn = createModernGradientButton("Appliquer les filtres", PRIMARY_COLOR, 13);
        JButton resetFiltreBtn = createModernButton("Réinitialiser", TEXT_SECONDARY, 12);
        
        appliquerFiltreBtn.addActionListener(e -> appliquerFiltres());
        resetFiltreBtn.addActionListener(e -> resetFiltres());
        
        filterPanel.add(filtreLabel);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Année:"));
        filterPanel.add(filtreAnneeCombo);
        filterPanel.add(new JLabel("Mois:"));
        filterPanel.add(filtreMoisCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(appliquerFiltreBtn);
        filterPanel.add(resetFiltreBtn);
        
        return filterPanel;
    }
    
    private JPanel createModernControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(5, 0.05f),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        totalStatsLabel = new JLabel("Chargement des données en cours...");
        totalStatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalStatsLabel.setForeground(TEXT_PRIMARY);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton actualiserButton = createModernGradientButton("🔄 Actualiser", SECONDARY_COLOR, 13);
        JButton exporterButton = createModernGradientButton("📊 Vue Détaillée", ACCENT_COLOR, 13);
        JButton imprimerButton = createModernButton("📤 Exporter Données", PRIMARY_COLOR, 12);
        
        actualiserButton.addActionListener(e -> chargerStatistiques());
        exporterButton.addActionListener(e -> showDetailedView());
        imprimerButton.addActionListener(e -> showExportOptions());
        
        buttonPanel.add(imprimerButton);
        buttonPanel.add(exporterButton);
        buttonPanel.add(actualiserButton);
        
        controlPanel.add(totalStatsLabel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        return controlPanel;
    }
    
    private void chargerStatistiques() {
        contentPanel.removeAll();
        
        // Animation de chargement
        showLoadingAnimation();
        
        SwingWorker<List<StatistiqueMensuelle>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<StatistiqueMensuelle> doInBackground() {
                try {
                    // Pas de délai artificiel, récupération directe
                    System.out.println("Début du chargement des statistiques...");
                    List<StatistiqueMensuelle> result = demandeDAO.getStatistiquesMensuelles();
                    System.out.println("Chargement terminé, " + (result != null ? result.size() : 0) + " éléments");
                    return result;
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<StatistiqueMensuelle> stats = get();
                    System.out.println("Traitement des données dans l'EDT...");
                    
                    if (stats == null) {
                        addErrorState("Erreur de connexion à la base de données");
                        return;
                    }
                    
                    if (stats.isEmpty()) {
                        addEmptyState();
                    } else {
                        afficherDonneesAvecStyle(stats);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur dans done(): " + e.getMessage());
                    e.printStackTrace();
                    addErrorState("Erreur: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private void afficherDonneesAvecStyle(List<StatistiqueMensuelle> stats) {
        if (stats == null || stats.isEmpty()) {
            addEmptyState();
            return;
        }
        
        // Calculer les totaux
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
        
        // Création de la structure moderne
        JPanel mainWrapper = new JPanel();
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setBackground(BACKGROUND_COLOR);
        mainWrapper.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // 1. CARTES DE KPI MODERNES
        JPanel kpiPanel = createModernKPICards(totalDemandes, totalEnCours, totalLivrees, totalRecues);
        mainWrapper.add(kpiPanel);
        mainWrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // 2. TABLEAU DES STATISTIQUES MENSUELLES AVEC STYLE MODERNE
        JPanel monthlyPanel = createModernMonthlyTable(stats);
        mainWrapper.add(monthlyPanel);
        
        contentPanel.add(mainWrapper, BorderLayout.NORTH);
        updateTotalStats(totalDemandes, totalEnCours, totalLivrees, totalRecues);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createModernKPICards(int totalDemandes, int totalEnCours, int totalLivrees, int totalRecues) {
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setBackground(BACKGROUND_COLOR);
        kpiPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 140));
        
        kpiPanel.add(createModernKPICard("📋", "Total Demandes", String.valueOf(totalDemandes), 
                                       PRIMARY_COLOR, "Toutes périodes", totalDemandes));
        kpiPanel.add(createModernKPICard("⏳", "En Cours", String.valueOf(totalEnCours), 
                                       WARNING_COLOR, "En attente", totalEnCours));
        kpiPanel.add(createModernKPICard("📦", "Reçues", String.valueOf(totalRecues), 
                                       SECONDARY_COLOR, "En traitement", totalRecues));
        kpiPanel.add(createModernKPICard("✅", "Livrées", String.valueOf(totalLivrees), 
                                       ACCENT_COLOR, "Terminées", totalLivrees));
        
        return kpiPanel;
    }
    
    private JPanel createModernKPICard(String icone, String titre, String valeur, Color couleur, String sousTitre, int count) {
        JPanel carte = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec ombre
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Bordure colorée en haut
                g2d.setColor(couleur);
                g2d.fillRoundRect(0, 0, getWidth(), 4, 20, 20);
            }
        };
        
        configureKPICard(carte, icone, titre, valeur, couleur, sousTitre);
        return carte;
    }
    
    private void configureKPICard(JPanel carte, String icone, String titre, String valeur, Color couleur, String sousTitre) {
        carte.setLayout(new BorderLayout(0, 10));
        carte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 20, 20, 20),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        carte.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // En-tête avec icône
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        
        JLabel iconeLabel = new JLabel(icone);
        iconeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconeLabel.setForeground(couleur);
        
        JLabel titreLabel = new JLabel(titre);
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titreLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(iconeLabel, BorderLayout.WEST);
        headerPanel.add(titreLabel, BorderLayout.CENTER);
        
        // Valeur principale
        JLabel valeurLabel = new JLabel(valeur);
        valeurLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valeurLabel.setForeground(TEXT_PRIMARY);
        
        // Sous-titre
        JLabel sousTitreLabel = new JLabel(sousTitre);
        sousTitreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sousTitreLabel.setForeground(TEXT_SECONDARY);
        
        carte.add(headerPanel, BorderLayout.NORTH);
        carte.add(valeurLabel, BorderLayout.CENTER);
        carte.add(sousTitreLabel, BorderLayout.SOUTH);
        
        // Effet de survol
        carte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                carte.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(couleur.brighter(), 1),
                    BorderFactory.createEmptyBorder(14, 19, 19, 19)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                carte.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(15, 20, 20, 20),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
        });
    }
    
    private JPanel createModernMonthlyTable(List<StatistiqueMensuelle> stats) {
        JPanel monthlyPanel = new JPanel();
        monthlyPanel.setLayout(new BoxLayout(monthlyPanel, BoxLayout.Y_AXIS));
        monthlyPanel.setBackground(BACKGROUND_COLOR);
        monthlyPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // En-tête de section moderne
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        JLabel sectionTitle = new JLabel("📅 STATISTIQUES GLOBALES");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_PRIMARY);
        
        JLabel countLabel = new JLabel(stats.size() + " période(s) de données");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(sectionTitle, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        monthlyPanel.add(headerPanel);
        
        // En-tête du tableau
        JPanel tableHeader = createTableHeader();
        monthlyPanel.add(tableHeader);
        monthlyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Lignes de données
        for (StatistiqueMensuelle stat : stats) {
            monthlyPanel.add(createModernTableRow(stat));
            monthlyPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        return monthlyPanel;
    }
    
    private JPanel createTableHeader() {
        JPanel header = new JPanel(new GridLayout(1, 6, 10, 0));
        header.setBackground(new Color(245, 247, 250));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        header.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        
        String[] headers = {"PÉRIODE", "TOTAL", "EN COURS", "REÇUES", "LIVRÉES", "TAUX LIVRAISON"};
        Color[] colors = {PRIMARY_COLOR, SECONDARY_COLOR, WARNING_COLOR, new Color(149, 117, 205), ACCENT_COLOR, PRIMARY_COLOR};
        
        for (int i = 0; i < headers.length; i++) {
            JLabel label = new JLabel(headers[i], SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(colors[i]);
            header.add(label);
        }
        
        return header;
    }
    
    private JPanel createModernTableRow(StatistiqueMensuelle stat) {
        JPanel row = new JPanel(new GridLayout(1, 6, 10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(3, 0.03f),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Calcul des pourcentages
        double tauxLivraison = stat.getTotalDemandes() > 0 ? 
            (double) stat.getDemandesLivrees() / stat.getTotalDemandes() * 100 : 0;
        
        // Cellules avec style moderne
        row.add(createTableCell("📅 " + stat.getMoisNom() + " " + stat.getAnnee(), PRIMARY_COLOR, true));
        row.add(createTableCell(String.valueOf(stat.getTotalDemandes()), SECONDARY_COLOR, true));
        row.add(createTableCell(String.valueOf(stat.getDemandesEnCours()), WARNING_COLOR, true));
        row.add(createTableCell(String.valueOf(stat.getDemandesRecues()), new Color(149, 117, 205), true));
        row.add(createTableCell(String.valueOf(stat.getDemandesLivrees()), ACCENT_COLOR, true));
        row.add(createTableCell(String.format("%.1f%%", tauxLivraison), tauxLivraison > 50 ? ACCENT_COLOR : WARNING_COLOR, true));
        
        // Effet de survol
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                row.setBackground(new Color(250, 250, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                row.setBackground(Color.WHITE);
            }
        });
        
        return row;
    }
    
    private JPanel createTableCell(String texte, Color couleur, boolean bold) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setOpaque(false);
        
        JLabel label = new JLabel(texte, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        label.setForeground(couleur);
        
        cell.add(label, BorderLayout.CENTER);
        return cell;
    }
    
    private void showLoadingAnimation() {
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(BACKGROUND_COLOR);
        loadingPanel.setBorder(new EmptyBorder(100, 0, 100, 0));
        
        JLabel loadingLabel = new JLabel("Chargement des données...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loadingLabel.setForeground(TEXT_SECONDARY);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 6));
        progressBar.setBackground(BACKGROUND_COLOR);
        progressBar.setForeground(PRIMARY_COLOR);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(loadingLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(progressBar);
        
        loadingPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(loadingPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void updateTotalStats(int totalDemandes, int totalEnCours, int totalLivrees, int totalRecues) {
        double tauxLivraison = totalDemandes > 0 ? (double) totalLivrees / totalDemandes * 100 : 0;
        String statsText = String.format(
            "<html><div style='text-align: left;'><b style='color:%s; font-size:14px;'>📊 VUE D'ENSEMBLE :</b><br>" +
            "<span style='color:%s; font-size:12px;'>%d demandes | %d en cours | %d reçues | %d livrées (<span style='color:%s;'>%.1f%%</span>)</span></div></html>",
            toHex(PRIMARY_COLOR), toHex(TEXT_SECONDARY), 
            totalDemandes, totalEnCours, totalRecues, totalLivrees,
            toHex(ACCENT_COLOR), tauxLivraison
        );
        totalStatsLabel.setText(statsText);
    }
    
    private void addEmptyState() {
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setBackground(BACKGROUND_COLOR);
        emptyPanel.setBorder(new EmptyBorder(80, 20, 80, 20));
        
        JLabel emptyIcon = new JLabel("📊", SwingConstants.CENTER);
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        emptyIcon.setForeground(TEXT_SECONDARY);
        
        JLabel emptyLabel = new JLabel("Aucune donnée statistique disponible", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyLabel.setForeground(TEXT_SECONDARY);
        emptyLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JLabel suggestionLabel = new JLabel("Les statistiques apparaîtront ici après l'enregistrement des demandes", SwingConstants.CENTER);
        suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        suggestionLabel.setForeground(TEXT_SECONDARY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.add(emptyIcon);
        textPanel.add(emptyLabel);
        textPanel.add(suggestionLabel);
        
        emptyPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(emptyPanel, BorderLayout.CENTER);
    }
    
    private void addErrorState(String message) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(BACKGROUND_COLOR);
        errorPanel.setBorder(new EmptyBorder(80, 20, 80, 20));
        
        JLabel errorIcon = new JLabel("❌", SwingConstants.CENTER);
        errorIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        errorIcon.setForeground(DANGER_COLOR);
        
        JLabel errorLabel = new JLabel("Erreur lors du chargement des statistiques", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        errorLabel.setForeground(DANGER_COLOR);
        errorLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JLabel detailLabel = new JLabel(message != null ? message : "Vérifiez la connexion à la base de données", SwingConstants.CENTER);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailLabel.setForeground(TEXT_SECONDARY);
        
        JButton retryButton = createModernButton("🔄 Réessayer", PRIMARY_COLOR, 12);
        retryButton.addActionListener(e -> chargerStatistiques());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(retryButton);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.add(errorIcon);
        textPanel.add(errorLabel);
        textPanel.add(detailLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        textPanel.add(buttonPanel);
        
        errorPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(errorPanel, BorderLayout.CENTER);
    }
    
    // Méthodes utilitaires pour les composants modernes
    private JButton createModernGradientButton(String text, Color backgroundColor, int fontSize) {
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
    
    private JButton createModernButton(String text, Color backgroundColor, int fontSize) {
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
    
    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        combo.setMaximumSize(new Dimension(180, 36));
        return combo;
    }
    
    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private Color darkenColor(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * (1 - factor)), 0),
            Math.max((int)(color.getGreen() * (1 - factor)), 0),
            Math.max((int)(color.getBlue() * (1 - factor)), 0)
        );
    }
    
    private Color brightenColor(Color color, float factor) {
        return new Color(
            Math.min((int)(color.getRed() * (1 + factor)), 255),
            Math.min((int)(color.getGreen() * (1 + factor)), 255),
            Math.min((int)(color.getBlue() * (1 + factor)), 255)
        );
    }
    
    public void refreshData() {
        chargerStatistiques();
    }
    
    private void appliquerFiltres() {
        String annee = (String) filtreAnneeCombo.getSelectedItem();
        String mois = (String) filtreMoisCombo.getSelectedItem();
        
        if (!"Toutes les années".equals(annee) || !"Tous les mois".equals(mois)) {
            JOptionPane.showMessageDialog(this,
                "Filtres appliqués:\nAnnée: " + annee + "\nMois: " + mois +
                "\n\nCette fonctionnalité sera implémentée dans la prochaine version.",
                "Filtres",
                JOptionPane.INFORMATION_MESSAGE);
        }
        chargerStatistiques();
    }
    
    private void resetFiltres() {
        filtreAnneeCombo.setSelectedIndex(0);
        filtreMoisCombo.setSelectedIndex(0);
        chargerStatistiques();
    }
    
    private void showExportOptions() {
        String[] options = {"📊 Exporter Toutes les Données", "📅 Exporter par Période", "📈 Générer Rapport Complet", "Annuler"};
        
        int choice = JOptionPane.showOptionDialog(this,
            "📤 OPTIONS D'EXPORTATION\n\n" +
            "Choisissez le type d'exportation souhaité:",
            "Export des Données",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0:
                DataExporter.exporterToutesDonnees(demandeDAO.getStatistiquesMensuelles(), this);
                break;
            case 1:
                DataExporter.exporterParPeriode(demandeDAO.getStatistiquesMensuelles(), this);
                break;
            case 2:
                DataExporter.genererRapportComplet(demandeDAO.getStatistiquesMensuelles(), this);
                break;
        }
    }
    
    private void showDetailedView() {
        List<StatistiqueMensuelle> stats = demandeDAO.getStatistiquesMensuelles();
        
        if (stats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucune donnée disponible pour afficher la vue détaillée.",
                "Données Manquantes",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        java.awt.Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof JFrame) {
            JOptionPane.showMessageDialog(this,
                "Vue détaillée - Fonctionnalité en développement",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Vue détaillée - Fonctionnalité en développement",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Classe pour les bordures avec ombre
    private class ShadowBorder implements javax.swing.border.Border {
        private int shadowSize;
        private float shadowOpacity;
        
        public ShadowBorder(int shadowSize, float shadowOpacity) {
            this.shadowSize = shadowSize;
            this.shadowOpacity = shadowOpacity;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (int i = 0; i < shadowSize; i++) {
                float opacity = shadowOpacity * (1.0f - (float) i / shadowSize);
                g2d.setColor(new Color(0, 0, 0, opacity));
                g2d.drawRoundRect(x + i, y + i, width - 1 - 2 * i, height - 1 - 2 * i, 10, 10);
            }
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}