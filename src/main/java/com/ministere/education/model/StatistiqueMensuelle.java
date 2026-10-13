package com.ministere.education.model;

public class StatistiqueMensuelle {
    private Long id;
    private int annee;
    private int mois;
    private int totalDemandes;
    private int demandesEnCours;
    private int demandesRecues;
    private int demandesLivrees;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    
    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }
    
    public int getTotalDemandes() { return totalDemandes; }
    public void setTotalDemandes(int totalDemandes) { this.totalDemandes = totalDemandes; }
    
    public int getDemandesEnCours() { return demandesEnCours; }
    public void setDemandesEnCours(int demandesEnCours) { this.demandesEnCours = demandesEnCours; }
    
    public int getDemandesRecues() { return demandesRecues; }
    public void setDemandesRecues(int demandesRecues) { this.demandesRecues = demandesRecues; }
    
    public int getDemandesLivrees() { return demandesLivrees; }
    public void setDemandesLivrees(int demandesLivrees) { this.demandesLivrees = demandesLivrees; }
    
    // Méthodes utilitaires
    public String getMoisNom() {
        String[] moisNoms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return mois >= 1 && mois <= 12 ? moisNoms[mois - 1] : "Inconnu";
    }
    
    public String getPeriode() {
        return getMoisNom() + " " + annee;
    }
    
    public double getTauxLivraison() {
        return totalDemandes > 0 ? (double) demandesLivrees / totalDemandes * 100 : 0;
    }
    
    public double getTauxReception() {
        return totalDemandes > 0 ? (double) demandesRecues / totalDemandes * 100 : 0;
    }
}