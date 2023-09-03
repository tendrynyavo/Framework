package model.actualite;

import connection.BddObject;
import connection.annotation.ColumnName;

public class Categorie extends BddObject {
    
    @ColumnName("id_categorie")
    String idCategorie;
    String nom;

    public String getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Categorie() throws Exception {
        this.setTable("categorie");
        this.setConnection("PostgreSQL");
        this.setPrimaryKeyName("idCategorie");
    }

    public Categorie(String idCategorie) throws Exception {
        this();
        this.setIdCategorie(idCategorie);
    }

}
