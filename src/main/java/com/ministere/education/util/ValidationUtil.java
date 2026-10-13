package com.ministere.education.util;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.regex.Pattern;

public class ValidationUtil {

    public static boolean validateRequiredFields(JTextComponent... fields) {
        for (JTextComponent field : fields) {
            if (field.getText() == null || field.getText().trim().isEmpty()) {
                field.requestFocus();
                return false;
            }
        }
        return true;
    }

    public static boolean isValidNumeroExamen(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        
        // Format: chiffres + 1 lettre + 2 chiffres
        // Exemples: 444402T20, 85677A22, 123B45
        String pattern = "^\\d+[A-Za-z]\\d{2}$";
        return Pattern.matches(pattern, numero.toUpperCase());
    }

    public static boolean isValidCIN(String cin) {
        return cin != null && cin.matches("\\d{12}");
    }

    public static boolean isValidAnneeExamen(String annee) {
        if (annee == null || !annee.matches("\\d{4}")) {
            return false;
        }
        
        try {
            int anneeValue = Integer.parseInt(annee);
            int currentYear = java.time.LocalDate.now().getYear();
            return anneeValue >= 1900 && anneeValue <= currentYear + 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}