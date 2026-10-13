package com.ministere.education.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Demande {
    private Long id;
    private String typeDiplome;
    private String nomComplet;
    private String numeroExamen;
    private Integer anneeExamen;
    private String lieuExamen;
    private String cin;
    private Date dateDemande;
    private Date dateReception;
    private Date dateLivraison;
    private String statut;
    private Boolean verrouille;
    private Date createdAt;
    private Date updatedAt;
    
    // Constructeurs
    public Demande() {
        this.verrouille = false;
        this.statut = "EN_COURS";
        this.dateDemande = new Date();
    }
    
    public Demande(String typeDiplome, String nomComplet, String numeroExamen, 
                  Integer anneeExamen, String lieuExamen, String cin) {
        this();
        this.typeDiplome = typeDiplome;
        this.nomComplet = nomComplet;
        this.numeroExamen = numeroExamen;
        this.anneeExamen = anneeExamen;
        this.lieuExamen = lieuExamen;
        this.cin = cin;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTypeDiplome() { return typeDiplome; }
    public void setTypeDiplome(String typeDiplome) { this.typeDiplome = typeDiplome; }
    
    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    
    public String getNumeroExamen() { return numeroExamen; }
    public void setNumeroExamen(String numeroExamen) { this.numeroExamen = numeroExamen; }
    
    public Integer getAnneeExamen() { return anneeExamen; }
    public void setAnneeExamen(Integer anneeExamen) { this.anneeExamen = anneeExamen; }
    
    public String getLieuExamen() { return lieuExamen; }
    public void setLieuExamen(String lieuExamen) { this.lieuExamen = lieuExamen; }
    
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    
    public Date getDateDemande() { return dateDemande; }
    public void setDateDemande(Date dateDemande) { this.dateDemande = dateDemande; }
    
    public Date getDateReception() { return dateReception; }
    public void setDateReception(Date dateReception) { this.dateReception = dateReception; }
    
    public Date getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(Date dateLivraison) { this.dateLivraison = dateLivraison; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public Boolean getVerrouille() { return verrouille; }
    public void setVerrouille(Boolean verrouille) { this.verrouille = verrouille; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    // Méthodes utilitaires pour l'affichage
    public String getAnneeExamenFormatted() {
        return anneeExamen != null ? anneeExamen.toString() : "";
    }
    
    public String getDateDemandeFormatted() {
        return formatDate(dateDemande);
    }
    
    public String getDateReceptionFormatted() {
        return formatDate(dateReception);
    }
    
    public String getDateLivraisonFormatted() {
        return formatDate(dateLivraison);
    }
    
    public String getCreatedAtFormatted() {
        return formatDate(createdAt);
    }
    
    public String getUpdatedAtFormatted() {
        return formatDate(updatedAt);
    }
    
    private String formatDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }
    
    public String getStatutDisplay() {
        switch (statut) {
            case "EN_COURS": return "En Cours";
            case "RECU": return "Reçu";
            case "LIVRE": return "Livré";
            default: return statut;
        }
    }
    
    public boolean isVerrouille() {
        return Boolean.TRUE.equals(verrouille);
    }
}