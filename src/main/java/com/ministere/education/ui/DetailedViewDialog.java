package com.ministere.education.ui;

import com.ministere.education.model.StatistiqueMensuelle;
import com.ministere.education.utils.DataExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class DetailedViewDialog extends JDialog {
    private List<StatistiqueMensuelle> stats;
    private final Color PRIMARY_COLOR = new Color(67, 97, 238);
    private final Color SECONDARY_COLOR = new Color(42, 157, 244);
    private final Color ACCENT_COLOR = new Color(56, 193, 114);
    private final Color WARNING_COLOR = new Color(255, 159, 28);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);

    public DetailedViewDialog(JFrame parent, List<StatistiqueMensuelle> stats) {
        super(parent, "Vue Détaillée des Statistiques", true);
        this.stats = stats;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(1200, 800);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("📊 VUE DÉTAILLÉE DES STATISTIQUES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Analyse complète des données mensuelles - " + stats.size() + " mois de données");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(textPanel, BorderLayout.WEST);

        // Tableau détaillé avec modèle personnalisé
        String[] columnNames = {
            "Période", "Total Demandes", "En Cours", "Reçues", "Livrées", 
            "Taux Livraison", "Taux Réception", "Performance"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 || columnIndex == 7 ? String.class : Integer.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Remplir le modèle avec les données
        for (StatistiqueMensuelle stat : stats) {
            double tauxLivraison = stat.getTauxLivraison();
            double tauxReception = stat.getTauxReception();
            
            Object[] rowData = {
                stat.getMoisNom() + " " + stat.getAnnee(),
                stat.getTotalDemandes(),
                stat.getDemandesEnCours(),
                stat.getDemandesRecues(),
                stat.getDemandesLivrees(),
                String.format("%.1f%%", tauxLivraison),
                String.format("%.1f%%", tauxReception),
                getPerformanceLabel(tauxLivraison)
            };
            model.addRow(rowData);
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowGrid(true);
        
        // Personnaliser l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(250, 250, 250));
        header.setForeground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        
        // Personnaliser le rendu des cellules
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Centrer le texte
                setHorizontalAlignment(SwingConstants.CENTER);
                
                // Couleurs alternées pour les lignes
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                }
                
                // Couleurs pour la colonne Performance
                if (column == 7) {
                    String performance = (String) value;
                    if (performance.contains("✅")) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if (performance.contains("⚠️")) {
                        c.setBackground(new Color(255, 255, 200));
                    } else if (performance.contains("❌")) {
                        c.setBackground(new Color(255, 220, 220));
                    }
                }
                
                // Couleurs pour les colonnes de pourcentages
                if (column == 5 || column == 6) {
                    try {
                        String strValue = value.toString().replace("%", "");
                        double percent = Double.parseDouble(strValue);
                        if (percent >= 80) {
                            setForeground(ACCENT_COLOR);
                        } else if (percent >= 60) {
                            setForeground(WARNING_COLOR);
                        } else {
                            setForeground(new Color(235, 87, 87));
                        }
                    } catch (NumberFormatException e) {
                        setForeground(TEXT_PRIMARY);
                    }
                } else {
                    setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        // Activer le tri
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // Définir des comparateurs personnalisés pour les colonnes
        sorter.setComparator(5, new PercentageComparator());
        sorter.setComparator(6, new PercentageComparator());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel d'actions
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Statistiques résumées
        JLabel statsLabel = createStatsSummary();
        actionPanel.add(statsLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton exporterBtn = createModernGradientButton("📤 Exporter ce rapport", ACCENT_COLOR, 12);
        JButton fermerBtn = createModernButton("Fermer", TEXT_SECONDARY, 12);

        exporterBtn.addActionListener(e -> {
            DataExporter.exporterToutesDonnees(stats, this);
        });

        fermerBtn.addActionListener(e -> dispose());

        buttonPanel.add(exporterBtn);
        buttonPanel.add(fermerBtn);
        
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createStatsSummary() {
        int totalDemandes = 0;
        int totalLivrees = 0;
        
        for (StatistiqueMensuelle stat : stats) {
            totalDemandes += stat.getTotalDemandes();
            totalLivrees += stat.getDemandesLivrees();
        }
        
        double tauxGlobal = totalDemandes > 0 ? (double) totalLivrees / totalDemandes * 100 : 0;
        
        String summary = String.format(
            "<html><div style='font-family: Segoe UI; font-size: 11px; color: %s;'>" +
            "<b>Résumé:</b> %d mois | %d demandes | Taux global: <b style='color: %s;'>%.1f%%</b>" +
            "</div></html>",
            toHex(TEXT_SECONDARY),
            stats.size(),
            totalDemandes,
            toHex(tauxGlobal >= 80 ? ACCENT_COLOR : tauxGlobal >= 60 ? WARNING_COLOR : new Color(235, 87, 87)),
            tauxGlobal
        );
        
        return new JLabel(summary);
    }
    
    private String getPerformanceLabel(double tauxLivraison) {
        if (tauxLivraison >= 80) {
            return "✅ Excellente";
        } else if (tauxLivraison >= 60) {
            return "⚠️ Moyenne";
        } else {
            return "❌ À améliorer";
        }
    }
    
    // Classe pour comparer les pourcentages dans le tri
    private class PercentageComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            try {
                double d1 = Double.parseDouble(s1.replace("%", "").trim());
                double d2 = Double.parseDouble(s2.replace("%", "").trim());
                return Double.compare(d1, d2);
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        }
    }
    
    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

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
}