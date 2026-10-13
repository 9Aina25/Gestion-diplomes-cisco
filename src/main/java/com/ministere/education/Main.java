package com.ministere.education;

import com.ministere.education.dao.DemandeDAO;
import com.ministere.education.ui.LoginDialog;
import com.ministere.education.ui.MainFrame;
import javax.swing.*;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
      
        DemandeDAO demandeDAO = new DemandeDAO();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            UIManager.put("OptionPane.background", new Color(248, 250, 252));
            UIManager.put("Panel.background", new Color(248, 250, 252));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
            
            if (loginDialog.isAuthenticated()) {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}