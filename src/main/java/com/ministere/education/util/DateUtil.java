package com.ministere.education.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static String formatDate(Date date) {
        if (date == null) return "";
        
        try {
            // Gestion spécifique pour java.sql.Date
            if (date instanceof java.sql.Date) {
                return ((java.sql.Date) date).toLocalDate().format(DATE_FORMATTER);
            } else {
                // Pour java.util.Date, utiliser SimpleDateFormat
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(date);
            }
        } catch (Exception e) {
            System.err.println("Erreur formatage date: " + e.getMessage());
            return "";
        }
    }
    
    public static String formatDate(java.sql.Date date) {
        if (date == null) return "";
        try {
            return date.toLocalDate().format(DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Erreur formatage sql.Date: " + e.getMessage());
            return "";
        }
    }
    
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Erreur parsing date: " + dateString);
            return null;
        }
    }
    
    public static String getTodayFormatted() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
}