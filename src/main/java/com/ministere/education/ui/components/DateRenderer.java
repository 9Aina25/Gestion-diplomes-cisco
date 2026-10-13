package com.ministere.education.ui.components;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;

public class DateRenderer extends DefaultTableCellRenderer {
    
    public DateRenderer() {
        super();
        setHorizontalAlignment(CENTER);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (value == null || value.toString().isEmpty() || value.toString().equals("--/--/----")) {
            setText("--/--/----");
        }
        
        return this;
    }
}