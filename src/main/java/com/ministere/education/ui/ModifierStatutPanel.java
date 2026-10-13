package com.ministere.education.ui;

import com.ministere.education.dao.DemandeDAO;
import com.ministere.education.model.Demande;
import com.ministere.education.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Date;

public class ModifierStatutPanel extends JPanel {
    private Demande demande;
    private DemandeDAO demandeDAO;
    private JComboBox<String> statutCombo;
    private JTextField dateReceptionField;
    private JTextField dateLivraisonField;
    private JButton saveButton, cancelButton;

    // Couleurs
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);

    public ModifierStatutPanel(Demande demande) {
        this.demande = demande;
        this.demandeDAO = new DemandeDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(500, 400));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("📋 MODIFICATION DU STATUT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(demande.getNomComplet());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Informations actuelles
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createInfoLabel("Numéro d'examen:"), gbc);
        gbc.gridx = 1;
        panel.add(createInfoValue(demande.getNumeroExamen()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createInfoLabel("Statut actuel:"), gbc);
        gbc.gridx = 1;
        panel.add(createInfoValue(demande.getStatut()), gbc);

        // Séparateur
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(createSeparator(), gbc);
        gbc.gridwidth = 1;

        // Nouveau statut
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Nouveau statut:*"), gbc);
        gbc.gridx = 1;
        statutCombo = new JComboBox<>(new String[]{"EN_COURS", "RECU", "LIVRE"});
        statutCombo.setSelectedItem(demande.getStatut());
        styleComboBox(statutCombo);
        statutCombo.addActionListener(e -> updateDateFields());
        panel.add(statutCombo, gbc);

        // Date réception
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Date réception:"), gbc);
        gbc.gridx = 1;
        dateReceptionField = createDateField();
        if (demande.getDateReception() != null) {
            dateReceptionField.setText(DateUtil.formatDate((java.sql.Date) demande.getDateReception()));
        }
        JPanel dateReceptionPanel = createDatePanel(dateReceptionField);
        panel.add(dateReceptionPanel, gbc);

        // Date livraison
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Date livraison:"), gbc);
        gbc.gridx = 1;
        dateLivraisonField = createDateField();
        if (demande.getDateLivraison() != null) {
            dateLivraisonField.setText(DateUtil.formatDate((java.sql.Date) demande.getDateLivraison()));
        }
        JPanel dateLivraisonPanel = createDatePanel(dateLivraisonField);
        panel.add(dateLivraisonPanel, gbc);

        updateDateFields();

        return panel;
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(229, 231, 235));
        return separator;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(107, 114, 128));
        return label;
    }

    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createDateField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JPanel createDatePanel(JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(Color.WHITE);
        panel.add(field, BorderLayout.CENTER);
        
        JButton dateButton = new JButton("📅");
        dateButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateButton.setPreferredSize(new Dimension(35, 30));
        dateButton.setBackground(new Color(243, 244, 246));
        dateButton.setBorder(new LineBorder(new Color(229, 231, 235), 1));
        dateButton.addActionListener(e -> showDatePicker(field));
        
        panel.add(dateButton, BorderLayout.EAST);
        return panel;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void showDatePicker(JTextField field) {
        JDialog dateDialog = new JDialog();
        dateDialog.setTitle("Sélectionner une date");
        dateDialog.setModal(true);
        dateDialog.setLayout(new BorderLayout());
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        if (!field.getText().isEmpty()) {
            try {
                LocalDate existingDate = DateUtil.parseDate(field.getText().trim());
                if (existingDate != null) {
                    dateSpinner.setValue(java.sql.Date.valueOf(existingDate));
                }
            } catch (Exception e) {
                dateSpinner.setValue(new Date());
            }
        } else {
            dateSpinner.setValue(new Date());
        }
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            field.setText(DateUtil.formatDate(selectedDate));
            dateDialog.dispose();
        });
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dateDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        dateDialog.add(dateSpinner, BorderLayout.CENTER);
        dateDialog.add(buttonPanel, BorderLayout.SOUTH);
        dateDialog.pack();
        dateDialog.setLocationRelativeTo(field);
        dateDialog.setVisible(true);
    }

    private void updateDateFields() {
        String statut = (String) statutCombo.getSelectedItem();
        
        switch (statut) {
            case "RECU":
                dateReceptionField.setEnabled(true);
                dateLivraisonField.setEnabled(false);
                dateLivraisonField.setText("");
                if (dateReceptionField.getText().isEmpty()) {
                    dateReceptionField.setText(DateUtil.getTodayFormatted());
                }
                break;
            case "LIVRE":
                dateReceptionField.setEnabled(true);
                dateLivraisonField.setEnabled(true);
                if (dateReceptionField.getText().isEmpty()) {
                    dateReceptionField.setText(DateUtil.getTodayFormatted());
                }
                if (dateLivraisonField.getText().isEmpty()) {
                    dateLivraisonField.setText(DateUtil.getTodayFormatted());
                }
                break;
            default:
                dateReceptionField.setEnabled(false);
                dateLivraisonField.setEnabled(false);
                dateReceptionField.setText("");
                dateLivraisonField.setText("");
                break;
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        cancelButton = new JButton("Annuler");
        saveButton = new JButton("Enregistrer");

        styleSecondaryButton(cancelButton);
        stylePrimaryButton(saveButton);

        cancelButton.addActionListener(e -> cancelModification());
        saveButton.addActionListener(e -> enregistrerModifications());

        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SUCCESS_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SUCCESS_COLOR);
            }
        });
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(107, 114, 128));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(87, 94, 108));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(107, 114, 128));
            }
        });
    }

    private void enregistrerModifications() {
        try {
            String nouveauStatut = (String) statutCombo.getSelectedItem();
            LocalDate dateReception = null;
            LocalDate dateLivraison = null;

            if ("RECU".equals(nouveauStatut) || "LIVRE".equals(nouveauStatut)) {
                if (dateReceptionField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "La date de réception est obligatoire pour le statut " + nouveauStatut + ".", 
                        "Date Obligatoire", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                dateReception = DateUtil.parseDate(dateReceptionField.getText().trim());
                if (dateReception == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Format de date de réception invalide. Utilisez jj/mm/aaaa.", 
                        "Date Invalide", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if ("LIVRE".equals(nouveauStatut)) {
                if (dateLivraisonField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "La date de livraison est obligatoire pour le statut LIVRE.", 
                        "Date Obligatoire", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                dateLivraison = DateUtil.parseDate(dateLivraisonField.getText().trim());
                if (dateLivraison == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Format de date de livraison invalide. Utilisez jj/mm/aaaa.", 
                        "Date Invalide", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (dateReception != null && dateReception.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, 
                    "La date de réception ne peut pas être dans le futur.", 
                    "Date Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dateLivraison != null && dateLivraison.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, 
                    "La date de livraison ne peut pas être dans le futur.", 
                    "Date Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dateReception != null && dateLivraison != null && dateLivraison.isBefore(dateReception)) {
                JOptionPane.showMessageDialog(this, 
                    "La date de livraison ne peut pas être antérieure à la date de réception.", 
                    "Dates Incohérentes", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            demande.setStatut(nouveauStatut);
            
            if (dateReception != null) {
                demande.setDateReception(java.sql.Date.valueOf(dateReception));
            } else {
                demande.setDateReception(null);
            }
            
            if (dateLivraison != null) {
                demande.setDateLivraison(java.sql.Date.valueOf(dateLivraison));
            } else {
                demande.setDateLivraison(null);
            }

            if (demandeDAO.updateDemande(demande)) {
                StringBuilder message = new StringBuilder();
                message.append("✅ Statut mis à jour avec succès!\n\n")
                      .append("Nouveau statut: ").append(nouveauStatut);
                
                if (dateReception != null) {
                    message.append("\nDate réception: ").append(DateUtil.formatDate(dateReception));
                }
                if (dateLivraison != null) {
                    message.append("\nDate livraison: ").append(DateUtil.formatDate(dateLivraison));
                }
                
                if ("LIVRE".equals(nouveauStatut)) {
                    message.append("\n\n⚠️ La demande est maintenant VERROUILLÉE et ne peut plus être modifiée.");
                }

                JOptionPane.showMessageDialog(this, 
                    message.toString(),
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de la mise à jour de la demande.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Une erreur est survenue: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cancelModification() {
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir annuler les modifications?",
            "Confirmation d'annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (response == JOptionPane.YES_OPTION) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        }
    }
}