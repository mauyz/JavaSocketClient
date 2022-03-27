/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javam1client.model;

/**
 *
 * @author Mauyz
 */

public class Client {
    
    private  Integer numero;
    private  String nom;
    private  String adresse;
    private Integer solde;

    public Client(Integer numero, String nom, String adresse, Integer solde) {
        this.numero = numero;
        this.nom = nom;
        this.adresse = adresse;
        this.solde = solde;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public int getSolde() {
        return solde;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setSolde(Integer solde) {
        this.solde = solde;
    }            
}
