package com.ministere.education.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean authenticated = false;
    
    private static final String[][] USERS = {
        {"admin", "admin123"},
        {"agent", "agent123"},
        {"user", "user123"}
    };
    
    public LoginDialog(Frame parent) {
        super(parent, "🔐 Authentification - Ministère de l'Éducation", true);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(450, 350);
        setResizable(false);
        setBackground(new Color(248, 250, 252));
        
        // En-tête avec gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Formulaire de connexion
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(loginButton);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 102, 204), 
                    getWidth(), 0, new Color(0, 153, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(400, 80));
        
        JLabel titleLabel = new JLabel("🎓 Gestion des Diplômes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("CEPE • BEPC");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(230, 230, 230));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Icône utilisateur
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel iconLabel = new JLabel("👤", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        panel.add(iconLabel, gbc);
        
        // Nom d'utilisateur
        gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Nom d'utilisateur:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = createStyledTextField();
        usernameField.setToolTipText("Entrez votre nom d'utilisateur");
        panel.add(usernameField, gbc);
        
        // Mot de passe
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel passLabel = new JLabel("Mot de passe:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = createStyledPasswordField();
        passwordField.setToolTipText("Entrez votre mot de passe");
        panel.add(passwordField, gbc);
        
        // Info utilisateurs
        gbc.gridy = 3; gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 248, 225));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel infoLabel = new JLabel("<html><b>📋 Comptes de test:</b><br>" +
            "• <b>admin</b> / admin123<br>" +
            "• <b>agent</b> / agent123<br>" +
            "• <b>user</b> / user123</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        panel.add(infoPanel, gbc);
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.setBackground(Color.WHITE);
        
        loginButton = new JButton("🔓 Se Connecter");
        JButton cancelButton = new JButton("🚪 Quitter");
        
        // Style des boutons
        stylePrimaryButton(loginButton);
        styleSecondaryButton(cancelButton);
        
        loginButton.addActionListener(new LoginAction());
        cancelButton.addActionListener(e -> {
            authenticated = false;
            dispose();
            System.exit(0);
        });
        
        panel.add(loginButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 76));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 133, 66), 1),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 133, 66));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 76));
            }
        });
    }
    
    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(88, 97, 105), 1),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(88, 97, 105));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(108, 117, 125));
            }
        });
    }
    
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                showError("Veuillez saisir le nom d'utilisateur et le mot de passe.");
                return;
            }
            
            if (checkCredentials(username, password)) {
                authenticated = true;
                showSuccess("Connexion réussie! Bienvenue, " + username + "!");
                dispose();
            } else {
                showError("Identifiants incorrects! Veuillez vérifier vos informations.");
                passwordField.setText("");
                usernameField.requestFocus();
            }
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            "❌ " + message,
            "Erreur d'Authentification",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            "✅ " + message,
            "Connexion Réussie",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean checkCredentials(String username, String password) {
        for (String[] user : USERS) {
            if (user[0].equals(username) && user[1].equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
}