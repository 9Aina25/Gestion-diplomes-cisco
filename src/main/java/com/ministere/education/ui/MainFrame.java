package com.ministere.education.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private CepePanel cepePanel;
    private BepcPanel bepcPanel;
    private NouvelleDemandePanel nouvelleDemandePanel;
    private JButton logoutButton;
    
    public MainFrame() {
        initComponents();
        setTitle("🎓 Gestion des Diplômes CEPE/BEPC - Ministère de l'Éducation");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Confirmation avant fermeture
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmLogout();
            }
        });
    }
    
    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Panel principal avec BorderLayout
        setLayout(new BorderLayout());
        
        // En-tête avec déconnexion
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Création des panels
        dashboardPanel = new DashboardPanel();
        cepePanel = new CepePanel();
        bepcPanel = new BepcPanel();
        nouvelleDemandePanel = new NouvelleDemandePanel();
        
        // Ajout des onglets avec icônes
        tabbedPane.addTab("📊 Tableau de Bord", dashboardPanel);
        tabbedPane.addTab("🏫 Diplômes CEPE", cepePanel);
        tabbedPane.addTab("🎓 Diplômes BEPC", bepcPanel);
        tabbedPane.addTab("📝 Nouvelle Demande", nouvelleDemandePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Mise à jour des données au changement d'onglet
        tabbedPane.addChangeListener(e -> refreshCurrentTab());
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(new Color(248, 250, 252));
        
        // Titre
        JLabel titleLabel = new JLabel("🎓 Gestion des Diplômes - Ministère de l'Éducation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Bouton de déconnexion
        logoutButton = new JButton("🚪 Déconnexion");
        styleLogoutButton(logoutButton);
        logoutButton.addActionListener(e -> confirmLogout());
        
        panel.add(logoutButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void styleLogoutButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 35, 51), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 53, 69));
            }
        });
    }
    
    private void confirmLogout() {
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir vous déconnecter ?",
            "Confirmation de déconnexion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (response == JOptionPane.YES_OPTION) {
            dispose();
            showLoginDialog();
        }
    }
    
    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(null);
        loginDialog.setVisible(true);
        
        if (loginDialog.isAuthenticated()) {
            // Recréer la fenêtre principale
            MainFrame newMainFrame = new MainFrame();
            newMainFrame.setVisible(true);
        } else {
            System.exit(0);
        }
    }
    
    private void refreshCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
        
        if (selectedComponent instanceof DashboardPanel) {
            ((DashboardPanel) selectedComponent).refreshData();
        } else if (selectedComponent instanceof CepePanel) {
            ((CepePanel) selectedComponent).refreshData();
        } else if (selectedComponent instanceof BepcPanel) {
            ((BepcPanel) selectedComponent).refreshData();
        } else if (selectedComponent instanceof NouvelleDemandePanel) {
            ((NouvelleDemandePanel) selectedComponent).refreshForm();
        }
    }
}