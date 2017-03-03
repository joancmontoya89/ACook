package montoya.girona.joan.idi.fib.acook;

/**
 * Created by joangmontoya on 30/12/15.
 */
public class Participa {

    /**
     * Atributs privats de la classe
     */
    private String nom_recepta;
    private String nom_ingredient;
    private int quantitat;


    /**
     * Metodes publics de la classe
     */
    public Participa(String nom_recepta, String nom_ingredient, int quantitat) {
        this.nom_recepta = nom_recepta;
        this.nom_ingredient = nom_ingredient;
        this.quantitat = quantitat;
    }

    public String getNom_recepta() {
        return nom_recepta;
    }

    public void setNom_recepta(String nom_recepta) {
        this.nom_recepta = nom_recepta;
    }

    public String getNom_ingredient() {
        return nom_ingredient;
    }

    public void setNom_ingredient(String nom_ingredient) {
        this.nom_ingredient = nom_ingredient;
    }

    public int getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(int quantitat) {
        this.quantitat = quantitat;
    }
}
