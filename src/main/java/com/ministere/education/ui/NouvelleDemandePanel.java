package com.ministere.education.ui;

import com.ministere.education.dao.DemandeDAO;
import com.ministere.education.model.Demande;
import com.ministere.education.util.ValidationUtil;
import com.ministere.education.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.util.Date;

public class NouvelleDemandePanel extends JPanel {
    private JTextField nomField, prenomField, cinField, numeroExamenField, lieuExamenField, anneeExamenField, dateDemandeField;
    private JComboBox<String> typeDiplomeCombo;
    private JButton submitButton, resetButton;
    private DemandeDAO demandeDAO;

    // Palette de couleurs
    private final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private final Color BORDER_COLOR = new Color(229, 231, 235);

    public NouvelleDemandePanel() {
        demandeDAO = new DemandeDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("📝 NOUVELLE DEMANDE DE DIPLÔME");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Formulaire d'enregistrement des demandes");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Type Diplôme
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("🎓 Type Diplôme:*"), gbc);
        gbc.gridx = 1;
        typeDiplomeCombo = new JComboBox<>(new String[]{"CEPE", "BEPC"});
        styleComboBox(typeDiplomeCombo);
        panel.add(typeDiplomeCombo, gbc);

        // Nom
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("👤 Nom:*"), gbc);
        gbc.gridx = 1;
        nomField = createTextField();
        panel.add(nomField, gbc);

        // Prénom
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("👤 Prénom:*"), gbc);
        gbc.gridx = 1;
        prenomField = createTextField();
        panel.add(prenomField, gbc);

        // CIN
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("🆔 CIN (12 chiffres):*"), gbc);
        gbc.gridx = 1;
        cinField = createTextField();
        ((AbstractDocument) cinField.getDocument()).setDocumentFilter(new DigitOnlyFilter(12));
        cinField.setToolTipText("12 chiffres uniquement");
        panel.add(cinField, gbc);

        // Numéro d'Examen
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("📝 Numéro d'Examen:*"), gbc);
        gbc.gridx = 1;
        numeroExamenField = createTextField();
        numeroExamenField.setToolTipText("Format: Chiffres et lettres (ex: 444402T20, 85677A22)");
        panel.add(numeroExamenField, gbc);

        // Année Examen
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("📅 Année d'Examen:*"), gbc);
        gbc.gridx = 1;
        anneeExamenField = createTextField();
        anneeExamenField.setToolTipText("4 chiffres (ex: 2024)");
        ((AbstractDocument) anneeExamenField.getDocument()).setDocumentFilter(new DigitOnlyFilter(4));
        panel.add(anneeExamenField, gbc);

        // Lieu Examen
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(createLabel("🏫 Lieu Examen:*"), gbc);
        gbc.gridx = 1;
        lieuExamenField = createTextField();
        panel.add(lieuExamenField, gbc);

        // Date de Demande
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(createLabel("📋 Date de Demande:*"), gbc);
        gbc.gridx = 1;
        dateDemandeField = createTextField();
        dateDemandeField.setText(DateUtil.getTodayFormatted());
        dateDemandeField.setToolTipText("Format: jj/mm/aaaa - Cliquez pour sélectionner une date");
        
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(dateDemandeField, BorderLayout.CENTER);
        
        JButton datePickerButton = new JButton("📅");
        datePickerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        datePickerButton.setPreferredSize(new Dimension(40, dateDemandeField.getPreferredSize().height));
        datePickerButton.addActionListener(e -> showDatePicker(dateDemandeField));
        
        datePanel.add(datePickerButton, BorderLayout.EAST);
        panel.add(datePanel, gbc);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    private void showDatePicker(JTextField field) {
        JDialog dateDialog = new JDialog();
        dateDialog.setTitle("Sélectionner une date");
        dateDialog.setModal(true);
        dateDialog.setLayout(new BorderLayout());
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        
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

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        submitButton = new JButton("💾 Enregistrer la Demande");
        resetButton = new JButton("🔄 Réinitialiser");

        stylePrimaryButton(submitButton);
        styleSecondaryButton(resetButton);

        submitButton.addActionListener(e -> enregistrerDemande());
        resetButton.addActionListener(e -> resetForm());

        panel.add(submitButton);
        panel.add(resetButton);

        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
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
        button.setBackground(TEXT_SECONDARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(TEXT_SECONDARY.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(TEXT_SECONDARY);
            }
        });
    }

    private void enregistrerDemande() {
        try {
            // Validation des champs obligatoires
            if (!ValidationUtil.validateRequiredFields(nomField, prenomField, cinField, 
                    numeroExamenField, lieuExamenField, anneeExamenField, dateDemandeField)) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez remplir tous les champs obligatoires (*).", 
                    "Champs Obligatoires Manquants", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validation CIN (exactement 12 chiffres)
            String cin = cinField.getText().trim();
            if (!cin.matches("\\d{12}")) {
                JOptionPane.showMessageDialog(this, 
                    "Le CIN doit contenir exactement 12 chiffres.", 
                    "CIN Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                cinField.requestFocus();
                cinField.selectAll();
                return;
            }

            // Vérification doublon CIN
            if (demandeDAO.isCinExists(cin)) {
                JOptionPane.showMessageDialog(this, 
                    "❌ DOUBLON DÉTECTÉ - Une demande existe déjà avec ce CIN:\n" +
                    "CIN: " + cin + "\n\n" +
                    "Un candidat ne peut faire qu'une seule demande de diplôme.",
                    "Doublon CIN Détecté", 
                    JOptionPane.ERROR_MESSAGE);
                cinField.requestFocus();
                cinField.selectAll();
                return;
            }

            // Validation année d'examen
String anneeExamenText = anneeExamenField.getText().trim();

// Vérification que le champ n'est pas vide
if (anneeExamenText.isEmpty()) {
    JOptionPane.showMessageDialog(this, 
        "Veuillez saisir une année d'examen.", 
        "Champ Obligatoire", 
        JOptionPane.ERROR_MESSAGE);
    anneeExamenField.requestFocus();
    return;
}

// Vérification du format (4 chiffres)
if (!anneeExamenText.matches("\\d{4}")) {
    JOptionPane.showMessageDialog(this, 
        "L'année d'examen doit contenir exactement 4 chiffres (ex: 2024).", 
        "Année Invalide", 
        JOptionPane.ERROR_MESSAGE);
    anneeExamenField.requestFocus();
    anneeExamenField.selectAll();
    return;
}

// Conversion en entier et validation
int anneeExamen = Integer.parseInt(anneeExamenText);
int anneeCourante = LocalDate.now().getYear();

// Vérification que l'année n'est pas dans le futur
if (anneeExamen > anneeCourante) {
    JOptionPane.showMessageDialog(this, 
        "L'année d'examen ne peut pas être dans le futur. L'année maximale est " + anneeCourante + ".", 
        "Année Invalide", 
        JOptionPane.ERROR_MESSAGE);
    anneeExamenField.requestFocus();
    anneeExamenField.selectAll();
    return;
}

// Validation optionnelle : année minimale (ex: 1900)
if (anneeExamen < 2004) {
    JOptionPane.showMessageDialog(this, 
        "L'année d'examen doit être supérieure à 2004.", 
        "Année Invalide", 
        JOptionPane.ERROR_MESSAGE);
    anneeExamenField.requestFocus();
    anneeExamenField.selectAll();
    return;
}
            
            if (anneeExamen < 1900 || anneeExamen > anneeCourante + 1) {
                JOptionPane.showMessageDialog(this, 
                    "L'année d'examen doit être comprise entre 1900 et " + (anneeCourante + 1) + ".", 
                    "Année Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                anneeExamenField.requestFocus();
                anneeExamenField.selectAll();
                return;
            }

            // Validation numéro d'examen
            String numeroExamen = numeroExamenField.getText().trim().toUpperCase();
            if (!ValidationUtil.isValidNumeroExamen(numeroExamen)) {
                JOptionPane.showMessageDialog(this, 
                    "❌ FORMAT NUMÉRO D'EXAMEN INVALIDE\n\n" +
                    "Le numéro d'examen doit respecter le format :\n" +
                    "• Chiffres + 1 Lettre + 2 Chiffres\n" +
                    "• Exemples valides : 444402T20, 85677A22, 123B45\n\n" +
                    "Votre saisie : " + numeroExamen,
                    "Numéro d'Examen Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                numeroExamenField.requestFocus();
                numeroExamenField.selectAll();
                return;
            }

            // Vérification doublon numéro d'examen
            if (demandeDAO.isNumeroExamenExists(numeroExamen)) {
                JOptionPane.showMessageDialog(this, 
                    "❌ DOUBLON DÉTECTÉ - Une demande existe déjà avec ce numéro d'examen:\n" +
                    "N° Examen: " + numeroExamen + "\n\n" +
                    "Veuillez vérifier les informations ou utiliser un autre numéro d'examen.",
                    "Doublon Détecté", 
                    JOptionPane.ERROR_MESSAGE);
                numeroExamenField.requestFocus();
                numeroExamenField.selectAll();
                return;
            }

            // Validation date de demande
            LocalDate dateDemande = DateUtil.parseDate(dateDemandeField.getText().trim());
            if (dateDemande == null) {
                JOptionPane.showMessageDialog(this, 
                    "Format de date de demande invalide. Utilisez jj/mm/aaaa.", 
                    "Date Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                dateDemandeField.requestFocus();
                dateDemandeField.selectAll();
                return;
            }

            if (dateDemande.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, 
                    "La date de demande ne peut pas être dans le futur.", 
                    "Date Invalide", 
                    JOptionPane.ERROR_MESSAGE);
                dateDemandeField.requestFocus();
                dateDemandeField.selectAll();
                return;
            }

            // Création de la demande
            Demande demande = new Demande();
            demande.setTypeDiplome((String) typeDiplomeCombo.getSelectedItem());
            demande.setNomComplet(nomField.getText().trim().toUpperCase() + " " + prenomField.getText().trim().toUpperCase());
            demande.setCin(cin);
            demande.setNumeroExamen(numeroExamen);
            demande.setAnneeExamen(anneeExamen); // CORRECTION : Utiliser int au lieu de String
            demande.setLieuExamen(lieuExamenField.getText().trim().toUpperCase());
            demande.setDateDemande(java.sql.Date.valueOf(dateDemande));
            demande.setStatut("EN_COURS");

            // Enregistrement de la demande
            if (demandeDAO.saveDemande(demande)) {
                StringBuilder message = new StringBuilder();
                message.append("✅ Demande enregistrée avec succès!\n\n")
                      .append("Nom complet: ").append(demande.getNomComplet()).append("\n")
                      .append("Type diplôme: ").append(demande.getTypeDiplome()).append("\n")
                      .append("N° Examen: ").append(demande.getNumeroExamen()).append("\n")
                      .append("Année examen: ").append(demande.getAnneeExamen()).append("\n")
                      .append("Date demande: ").append(DateUtil.formatDate(dateDemande)).append("\n")
                      .append("Lieu examen: ").append(demande.getLieuExamen());

                JOptionPane.showMessageDialog(this, 
                    message.toString(),
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erreur lors de l'enregistrement dans la base de données.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de format numérique: " + ex.getMessage(), 
                "Erreur de Format", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Une erreur est survenue: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetForm() {
        nomField.setText("");
        prenomField.setText("");
        cinField.setText("");
        numeroExamenField.setText("");
        lieuExamenField.setText("");
        anneeExamenField.setText("");
        typeDiplomeCombo.setSelectedIndex(0);
        dateDemandeField.setText(DateUtil.getTodayFormatted());
        nomField.requestFocus();
    }

    // AJOUT : Méthode refreshForm() pour résoudre l'erreur dans MainFrame
    public void refreshForm() {
        resetForm();
    }

    // Filtre pour n'accepter que les chiffres avec limite de longueur
    private class DigitOnlyFilter extends DocumentFilter {
        private final int maxLength;

        public DigitOnlyFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            if (string == null) return;
            
            String newString = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
            if (isValid(newString)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            if (text == null) return;
            
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            
            if (isValid(newText)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(String text) {
            return text.matches("\\d{0," + maxLength + "}");
        }
    }
}