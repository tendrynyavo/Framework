package model.classe;

import etu2070.annotation.Scope;
import etu2070.annotation.url;
import etu2070.annotation.auth;
import etu2070.annotation.restAPI;
import etu2070.annotation.session;
import etu2070.framework.FileUpload;
import etu2070.framework.ModelView;
import java.lang.reflect.Method;
import java.util.HashMap;

@Scope("singleton")
public class Classe {

    Integer id;
    String name;
    FileUpload image;
    HashMap<String, Object> session;

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(FileUpload image) {
        this.image = image;
    }

    public FileUpload getImage() {
        return image;
    }

    public Classe() {

    }

    public Classe(Integer id, String name) {
        this.setId(id);
        this.setName(name);
    }

    @url("classe-save.do")
    public String insert(Integer id, String name, String departement) {
        return id + " " + name + " " + departement;
    }



    @url("formulaire.do")
    public ModelView formulaire() {
        ModelView view = new ModelView("form-classe");
        // view.addSession("formulaire", classe);
        return view;
    }

    
    @session
    @auth("secretaire")
    @url("upload.do")
    public String upload() {
        // Classe classe = (Classe) this.getSession().get("classe");
        return "Mety ah";
    }

    @restAPI
    @url("find-all.do")
    public Classe[] findAll() {
        Classe[] classes = new Classe[3];
        classes[0] = new Classe(1, "Salle 1");
        classes[1] = new Classe(2, "Salle 2");
        classes[2] = new Classe(3, "Salle 3");
        return classes;
    }

    @restAPI
    @url("find-by-id.do")
    public Classe findById() {
        return new Classe(2, "Salle 4");
    }

}