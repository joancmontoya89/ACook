package montoya.girona.joan.idi.fib.acook;

/**
 * Created by joangmontoya on 8/1/16.
 */
public class ReceptesEntrada {

    /**
     * Atributs privats de la classe
     */
    private String nom_recepta;
    private String img_recepta;

    /**
     * Metodes publics de la classe
     */
    public ReceptesEntrada(String nom_recepta, String img_recepta) {
        this.nom_recepta = nom_recepta;
        this.img_recepta = img_recepta;
    }

    public String getImg_recepta() {
        return img_recepta;
    }

    public void setImg_recepta(String img_recepta) {
        this.img_recepta = img_recepta;
    }

    public String getNom_recepta() {
        return nom_recepta;
    }

    public void setNom_recepta(String nom_recepta) {
        this.nom_recepta = nom_recepta;
    }
}
