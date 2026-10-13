package com.ministere.education.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class CustomTable extends JTable {
    
    public CustomTable(TableModel model) {
        super(model);
        setRowHeight(25);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        if (getColumnCount() > 6) {
            getColumnModel().getColumn(3).setCellRenderer(new DateRenderer());
            getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());
            getColumnModel().getColumn(5).setCellRenderer(new DateRenderer());
            getColumnModel().getColumn(6).setCellRenderer(new DateRenderer());
        }
    }
}