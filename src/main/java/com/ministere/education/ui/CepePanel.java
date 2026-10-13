package com.ministere.education.ui;

import com.ministere.education.dao.DemandeDAO;
import com.ministere.education.model.Demande;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CepePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private DemandeDAO demandeDAO;

    // Palette de couleurs
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color SECONDARY_COLOR = new Color(99, 102, 241);
    private final Color ACCENT_COLOR = new Color(16, 185, 129);
    private final Color WARNING_COLOR = new Color(245, 158, 11);
    private final Color DANGER_COLOR = new Color(239, 68, 68);
    private final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private final Color BORDER_COLOR = new Color(229, 231, 235);

    public CepePanel() {
        demandeDAO = new DemandeDAO();
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));
        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 25, 25, 25));

        JPanel controlPanel = createModernControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createModernHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(37, 99, 235));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(getWidth(), 100));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel titleLabel = new JLabel("🏫 GESTION DES DIPLÔMES CEPE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Tableau de bord administratif");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        textPanel.add(Box.createVerticalGlue());

        header.add(textPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createModernControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Section Recherche
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // Champ de recherche
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 15, 8, 15)
        ));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(TEXT_PRIMARY);

        searchField.setText("Rechercher un candidat...");
        searchField.setForeground(TEXT_SECONDARY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Rechercher un candidat...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher un candidat...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        leftPanel.add(searchField);

        // Filtre Combo
        String[] statuts = {"Tous les statuts", "EN_COURS", "RECU", "LIVRE"};
        filterCombo = new JComboBox<>(statuts);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        filterCombo.setPreferredSize(new Dimension(150, 35));
        filterCombo.addActionListener(e -> filterTable());
        leftPanel.add(filterCombo);

        // Boutons à droite
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton refreshButton = createModernButton("Actualiser", Color.WHITE, TEXT_PRIMARY);
        JButton addButton = createModernButton("+ Nouvelle Demande", PRIMARY_COLOR, Color.WHITE);

        refreshButton.addActionListener(e -> refreshData());
        addButton.addActionListener(e -> showAddDemandeDialog());

        rightPanel.add(refreshButton);
        rightPanel.add(addButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Modèle de table
        String[] columnNames = {"#", "Candidat", "N° Examen", "Année Exam", "Statut", "Date Demande", "Date Réception", "Date Livraison", "Actions"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Aucune cellule n'est éditable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BACKGROUND_COLOR);
        table.setSelectionBackground(new Color(243, 244, 246));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header de la table
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setForeground(TEXT_SECONDARY);
        tableHeader.setPreferredSize(new Dimension(0, 40));

        // Centrage du header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Renderers personnalisés
        table.getColumnModel().getColumn(0).setCellRenderer(new CenterRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new LeftRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new CenterRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new CenterRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionsRenderer());

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Ajouter un listener de clic sur la table pour gérer les boutons
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                
                if (column == 8 && row >= 0) {
                    // Cliqué sur la colonne Actions, trouver la demande correspondante
                    int modelRow = table.convertRowIndexToModel(row);
                    String numeroExamen = table.getModel().getValueAt(modelRow, 2).toString();
                    Demande demande = findDemandeByNumeroExamen(numeroExamen);
                    
                    if (demande != null) {
                        // Déterminer quel bouton a été cliqué basé sur la position X
                        int x = e.getX() - table.getCellRect(row, column, false).x;
                        
                        if (x < 80) {
                            // Bouton Modifier
                            if (!"LIVRE".equals(demande.getStatut())) {
                                ouvrirModification(demande);
                            }
                        } else if (x < 160) {
                            // Bouton Détails
                            showDetails(demande);
                        } else if (x < 240) {
                            // Bouton Supprimer
                            if (!"LIVRE".equals(demande.getStatut())) {
                                confirmDelete(demande);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    // Méthode utilitaire pour trouver une demande par numéro d'examen
    private Demande findDemandeByNumeroExamen(String numeroExamen) {
        try {
            List<Demande> demandes = demandeDAO.getDemandesByType("CEPE");
            for (Demande d : demandes) {
                if (d.getNumeroExamen().equals(numeroExamen)) {
                    return d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Renderers personnalisés
    private class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
    }

    private class LeftRenderer extends DefaultTableCellRenderer {
        public LeftRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBorder(new EmptyBorder(0, 10, 0, 0));
        }
    }

    private class DateRenderer extends DefaultTableCellRenderer {
        public DateRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setForeground(TEXT_SECONDARY);
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value == null || value.toString().isEmpty()) {
                setText("-");
                setForeground(Color.LIGHT_GRAY);
            } else {
                setForeground(TEXT_SECONDARY);
            }
            return c;
        }
    }

    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                String statut = value != null ? value.toString() : "";

                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(new EmptyBorder(4, 12, 4, 12));

                switch (statut) {
                    case "EN_COURS":
                        label.setBackground(new Color(254, 243, 199));
                        label.setForeground(new Color(180, 83, 9));
                        label.setText("⏳ EN COURS");
                        break;
                    case "RECU":
                        label.setBackground(new Color(224, 231, 255));
                        label.setForeground(new Color(67, 56, 202));
                        label.setText("📦 REÇU");
                        break;
                    case "LIVRE":
                        label.setBackground(new Color(209, 250, 229));
                        label.setForeground(new Color(6, 95, 70));
                        label.setText("✅ LIVRÉ");
                        break;
                    default:
                        label.setBackground(Color.LIGHT_GRAY);
                        label.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                }
            }
            return c;
        }
    }

    private class ActionsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof JPanel) {
                JPanel panel = (JPanel) value;
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                return panel;
            }

            // Retourner un panel vide par défaut
            JPanel panel = new JPanel();
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }
    }

    private void filterTable() {
        try {
            final String searchText;
            String searchTextTemp = searchField.getText().toLowerCase();
            if (searchTextTemp.equals("rechercher un candidat...")) {
                searchText = "";
            } else {
                searchText = searchTextTemp;
            }

            final String selectedStatut = (String) filterCombo.getSelectedItem();

            RowFilter<DefaultTableModel, Integer> rf = new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    try {
                        String nom = entry.getStringValue(1).toLowerCase();
                        String numExamen = entry.getStringValue(2).toLowerCase();
                        boolean textMatch = searchText.isEmpty() ||
                                nom.contains(searchText) || numExamen.contains(searchText);

                        String statut = entry.getStringValue(4);
                        boolean statusMatch = "Tous les statuts".equals(selectedStatut) ||
                                statut.equals(selectedStatut);

                        return textMatch && statusMatch;
                    } catch (Exception e) {
                        return true;
                    }
                }
            };

            sorter.setRowFilter(rf);
        } catch (Exception e) {
            // Ignorer les erreurs de filtrage temporaires
        }
    }

    public void refreshData() {
        SwingWorker<List<Demande>, Void> worker = new SwingWorker<List<Demande>, Void>() {
            @Override
            protected List<Demande> doInBackground() {
                try {
                    return demandeDAO.getDemandesByType("CEPE");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    List<Demande> demandes = get();
                    if (demandes != null) {
                        // Sauvegarder le filtre actuel
                        RowFilter<? super DefaultTableModel, ? super Integer> savedFilter = sorter != null ? sorter.getRowFilter() : null;

                        table.setRowSorter(null);
                        tableModel.setRowCount(0);

                        int count = 1;
                        for (Demande d : demandes) {
                            Object[] rowData = {
                                String.valueOf(count++),
                                d.getNomComplet(),
                                d.getNumeroExamen(),
                                d.getAnneeExamen(),
                                d.getStatut(),
                                d.getDateDemandeFormatted(),
                                d.getDateReception() != null ? d.getDateReceptionFormatted() : "",
                                d.getDateLivraison() != null ? d.getDateLivraisonFormatted() : "",
                                createActionButtons(d)
                            };
                            tableModel.addRow(rowData);
                        }

                        sorter = new TableRowSorter<>(tableModel);
                        table.setRowSorter(sorter);

                        // Restaurer le filtre
                        if (savedFilter != null) {
                            sorter.setRowFilter(savedFilter);
                        }

                        // Forcer la mise à jour de l'affichage
                        table.revalidate();
                        table.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CepePanel.this,
                            "Erreur lors du chargement des données: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private JPanel createActionButtons(Demande demande) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(250, 35));

        // Créer les boutons sans ActionListener (gérés par le click sur la table)
        JButton editBtn = createStyledButton("Modifier", SECONDARY_COLOR);
        JButton detailBtn = createStyledButton("Détails", TEXT_SECONDARY);
        JButton deleteBtn = createStyledButton("Supprimer", DANGER_COLOR);

        // Vérifier si le statut est "LIVRE" pour désactiver les boutons
        boolean isLivree = "LIVRE".equals(demande.getStatut());

        if (isLivree) {
            editBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            editBtn.setBackground(Color.GRAY);
            deleteBtn.setBackground(Color.GRAY);
            editBtn.setToolTipText("Demande livrée - Modification impossible");
            deleteBtn.setToolTipText("Demande livrée - Suppression impossible");
        } else {
            editBtn.setToolTipText("Modifier le statut");
            deleteBtn.setToolTipText("Supprimer la demande");
        }
        detailBtn.setToolTipText("Voir les détails");

        panel.add(editBtn);
        panel.add(detailBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 25));
        
        return btn;
    }

    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (bg.equals(Color.WHITE)) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(10, 20, 10, 20)
            ));
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (bg.equals(Color.WHITE)) {
                    btn.setBackground(new Color(245, 245, 245));
                } else {
                    btn.setBackground(bg.darker());
                }
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    // Méthodes d'action
    private void showAddDemandeDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouvelle Demande CEPE");
        dialog.setModal(true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        NouvelleDemandePanel nouvelleDemandePanel = new NouvelleDemandePanel();
        dialog.add(nouvelleDemandePanel);
        dialog.setVisible(true);

        refreshData();
    }

    private void ouvrirModification(Demande d) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier le statut - " + d.getNomComplet());
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ModifierStatutPanel modifPanel = new ModifierStatutPanel(d);
        dialog.setContentPane(modifPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshData();
    }

    private void showDetails(Demande d) {
        String details = String.format(
                "📋 DÉTAILS DU CANDIDAT\n\n" +
                        "Nom complet: %s\n" +
                        "Numéro d'examen: %s\n" +
                        "Année d'examen: %s\n" +
                        "Date de demande: %s\n" +
                        "Statut: %s\n" +
                        "Lieu d'examen: %s\n" +
                        "CIN: %s\n" +
                        "Date réception: %s\n" +
                        "Date livraison: %s",
                d.getNomComplet(),
                d.getNumeroExamen(),
                d.getAnneeExamen(),
                d.getDateDemandeFormatted(),
                d.getStatut(),
                d.getLieuExamen(),
                d.getCin() != null ? d.getCin() : "Non renseigné",
                d.getDateReception() != null ? d.getDateReceptionFormatted() : "Non renseignée",
                d.getDateLivraison() != null ? d.getDateLivraisonFormatted() : "Non renseignée"
        );
        JOptionPane.showMessageDialog(this, details, "Détails du candidat", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmDelete(Demande d) {
        int response = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer la demande de " + d.getNomComplet() + "?\n" +
                        "Numéro d'examen: " + d.getNumeroExamen(),
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> deleteWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        return demandeDAO.deleteDemande(d.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(CepePanel.this,
                                    "Demande supprimée avec succès",
                                    "Succès",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(CepePanel.this,
                                    "Erreur lors de la suppression de la demande",
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(CepePanel.this,
                                "Erreur lors de la suppression: " + e.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            deleteWorker.execute();
        }
    }
}