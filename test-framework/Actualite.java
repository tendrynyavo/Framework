package model.actualite;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Connection;
import connection.BddObject;
import connection.Column;
import connection.annotation.ColumnName;
import etu2070.framework.FileUpload;
import etu2070.framework.ModelView;
import etu2070.annotation.auth;
import etu2070.annotation.url;
import etu2070.annotation.csv;
import formulaire.Formulaire;
import model.actualite.Categorie;

public class Actualite extends BddObject {

    @ColumnName("id_actualite")
    String idActualite;
    String titre;
    Categorie categorie;
    Timestamp date;
    String photo;
    FileUpload image;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) throws Exception {
        if (photo.isEmpty()) throw new Exception("Champ photo est vide");
        this.photo = photo;
    }

    public String getIdActualite() {
        return idActualite;
    }

    public void setIdActualite(String idActualite) throws Exception {
        if (idActualite.isEmpty()) throw new Exception("ID de l'actualite est vide");
        this.idActualite = idActualite;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) throws Exception {
        this.titre = titre;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public FileUpload getImage() {
        return image;
    }

    public void setImage(FileUpload image) {
        this.image = image;
    }

    public Actualite() throws Exception {
        this.setTable("actualite");
        this.setConnection("PostgreSQL");
        this.setPrimaryKeyName("idActualite");

        // Pour generer des sequences
        this.setFunctionPK("nextval('seq_id_actualite')");
        this.setCountPK(7); // Nombre total de caractere pour la sequence
        this.setPrefix("ACT"); // Prefix personnaliser
    }

    public static Formulaire createFormulaire() throws Exception {
        Formulaire form = Formulaire.createFormulaire(new Actualite(), "/test-framework/insert.do");
        form.getListeChamp()[4].setType("file");
        form.getListeChamp()[4].setName("image");
        form.getListeChamp()[5].setVisible(false, "");
        form.setTitle("Saisie d'actualite");
        return form;
    }
    
    @url("formulaire.do")
    public ModelView formulaire() throws Exception {
        ModelView view = new ModelView("form-actu");
        view.addItem("formulaire", createFormulaire());
        return view;
    }
    
    @auth
    @url("insert.do")
    public ModelView insertActualite() throws Exception {
        ModelView view = new ModelView();
        view.sendRedirect("/test-framework/formulaire.do");
        this.setPhoto(this.getImage().getName());
        this.getImage().setPath("/assets/img/");
        this.getImage().upload();
        this.insert(null);
        return view;
    }
    
    @url("authentification.do")
    public ModelView authentification() {
        ModelView view = new ModelView();
        view.addSession("isconnected", true);
        view.sendRedirect("/test-framework/formulaire.do");
        return view;
    }
    
    @url("log-out.do")
    public ModelView disconnected() {
        ModelView view = new ModelView();
        view.setInvalidate(true);
        view.sendRedirect("/test-framework/formulaire.do");
        return view;
    }

    public void insert(Connection connection, Column... args) throws Exception {
        boolean connect = false;
        try {
            if (connection == null) {connection = this.getConnection(); connect = true;}
            this.setIdActualite(this.getSequence().buildPrimaryKey(connection));
            this.setImage(null);
            super.insert(connection, args);
            if (connect) connection.commit();
        } catch (Exception e) {
            if (connect) connection.rollback();
            throw e;
        } finally {
            if (connect) connection.close();
        }
    }

    @url("liste-actualite.do")
    public ModelView liste() throws Exception {
        ModelView view = new ModelView("liste-actu");
        this.setTable("v_actualite");
        view.addItem("actualites", (Actualite[]) this.findAll(null));
        return view;
    }

    @csv({"id", "titre", "categorie", "date"})
    @url("csv-actu.do")
    public Actualite[] csv(String d) throws Exception {
        this.setTable((d != null) ? "v_actualite WHERE date <= '" + d + "'" : "v_actualite");
        return (Actualite[]) this.findAll(null);
    }

    public String toCsv() {
        return this.getIdActualite() + "," + this.getTitre()  + "," + this.getCategorie().getNom() + "," + this.getDate().toString();
    }

}