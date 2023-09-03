package model.etudiant;

import etu2070.annotation.url;
import model.classe.Classe;
import etu2070.framework.ModelView;
import formulaire.Formulaire;

public class Etudiant {

    Integer id;
    String name;
    Classe classe;

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Etudiant() {

    }

    public Etudiant(Integer id, String name) {
        this.setId(id);
        this.setName(name);
    }

    @url("formulaire-etudiant.do")
    public ModelView formulaire() throws Exception {
        ModelView view = new ModelView("form-etudiant");
        Formulaire form = createFormulaire();
        view.addItem("formulaire", form);
        return view;
    }

    public Formulaire createFormulaire() throws Exception {
        Formulaire form = Formulaire.createFormulaire(new Etudiant());
        return form;
    }
    
    @url("update-etudiant-all.do")
    public ModelView update() {
        ModelView view = new ModelView();
        view.addItem("etudiant", new Etudiant[] {new Etudiant(2, "Fabien"), new Etudiant(3, "Tendry")});
        view.addItem("classe", new Classe(2, "6 II"));
        view.setJson(true);
        return view;
    }

}