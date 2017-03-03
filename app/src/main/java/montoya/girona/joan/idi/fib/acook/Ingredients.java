package montoya.girona.joan.idi.fib.acook;

/**
 * Created by joangmontoya on 2/12/15.
 */
public class Ingredients {

    /* Atributs privats de la classe */
    private String nom;
    private String mesura;      //g, kg, l, ml, ...

    /* Metodes publics de la classe */
    public Ingredients(String nom, String mesura) {
        this.nom = nom;
        this.mesura = mesura;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMesura() {
        return mesura;
    }

    public void setMesura(String mesura) {
        this.mesura = mesura;
    }

}
