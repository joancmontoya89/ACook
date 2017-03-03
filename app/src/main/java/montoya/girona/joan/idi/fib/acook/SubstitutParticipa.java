package montoya.girona.joan.idi.fib.acook;

/**
 * Created by joangmontoya on 30/12/15.
 */
public class SubstitutParticipa {

    /**
     * Atributs privats de la classe
     */
    private String nom_recepta;
    private String nom_ingredient;
    private String substitut;


    /**
     * Metodes publics de la classe
     */
    public SubstitutParticipa(String nom_recepta, String nom_ingredient, String substitut) {
        this.nom_recepta = nom_recepta;
        this.nom_ingredient = nom_ingredient;
        this.substitut = substitut;
    }

    public String getNom_ingredient() {
        return nom_ingredient;
    }

    public void setNom_ingredient(String nom_ingredient) {
        this.nom_ingredient = nom_ingredient;
    }

    public String getNom_recepta() {
        return nom_recepta;
    }

    public void setNom_recepta(String nom_recepta) {
        this.nom_recepta = nom_recepta;
    }

    public String getSubstitut() {
        return substitut;
    }

    public void setSubstitut(String substitut) {
        this.substitut = substitut;
    }

    @Override
    public boolean equals(Object o) {
        SubstitutParticipa substitutParticipa = (SubstitutParticipa)o;
        boolean son_iguals = (substitutParticipa.getNom_recepta().contentEquals(nom_recepta));
        son_iguals = son_iguals &&
                (substitutParticipa.getNom_ingredient().contentEquals(nom_ingredient));
        son_iguals = son_iguals &&
                (substitutParticipa.getSubstitut().contentEquals(substitut));
        return son_iguals;
    }

    public boolean major_que(SubstitutParticipa sp) {
        String recsp = sp.getNom_recepta();
        String ingsp = sp.getNom_ingredient();
        String subsp = sp.getSubstitut();
        boolean res;
        if (nom_recepta.compareTo(recsp) < 0) res = false;
        else if (nom_recepta.compareTo(recsp) > 0) res = true;
        else {  // mateixa recepta
            if (nom_ingredient.compareTo(ingsp) < 0) res = false;
            else if (nom_ingredient.compareTo(ingsp) > 0) res = true;
            else {
                if (substitut.compareTo(subsp) < 0) res = false;
                else res = true;
            }
        }
        return res;
    }
}
