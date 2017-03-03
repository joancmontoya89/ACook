package montoya.girona.joan.idi.fib.acook;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by joangmontoya on 27/11/15.
 */
public class Contract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "montoya.girona.joan.idi.fib.acook.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().build();

    public static final String TABLE_RECEPTES = "receptes";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE " + TABLE_RECEPTES + " (" +
                    TRecepta.COLUMN_NOM + " " + TEXT_TYPE + " PRIMARY KEY," +   //nom de la recepta
                    TRecepta.COLUMN_DESCRIPCIO + " " + TEXT_TYPE + COMMA_SEP + //descripcio recepta
                    TRecepta.COLUMN_TIPUS + " " + TEXT_TYPE + COMMA_SEP +       //tipus recepta
                    TRecepta.COLUMN_IMATGE +  " " + TEXT_TYPE + " )";           //imatge recepta

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                    TIngredients.COLUMN_NOM + " " + TEXT_TYPE + " PRIMARY KEY," + //nom de la recepta
                    TIngredients.COLUMN_MESURA + " " + TEXT_TYPE + " " +
                    "NOT NULL )";                               //mesura de l'ing, ml, g,

    public static final String TABLE_PARTICIPA = "participa";       //ing participa en recepta
    public static final String SQL_CREATE_ENTRIES3 =
            "CREATE TABLE " + TABLE_PARTICIPA + " (" +
                    TParticipa.COLUMN_RECEPTA +  " " + TEXT_TYPE + COMMA_SEP +     //nom de la recepta
                    TParticipa.COLUMN_INGREDIENT +  " " + TEXT_TYPE + COMMA_SEP +  //ingredient participant
                    TParticipa.COLUMN_QUANTITAT + " INTEGER CHECK(" +
                    TParticipa.COLUMN_QUANTITAT + " > 0)" + COMMA_SEP + //qtt ingredient
                    " PRIMARY KEY (" + TParticipa.COLUMN_RECEPTA + COMMA_SEP +
                    TParticipa.COLUMN_INGREDIENT + " )" + COMMA_SEP +       //PK composta
                    "FOREIGN KEY(" + TParticipa.COLUMN_RECEPTA + ") REFERENCES " +
                    TABLE_RECEPTES + "(" + TRecepta.COLUMN_NOM + "), " +    //FK recepta
                    "FOREIGN KEY(" + TParticipa.COLUMN_INGREDIENT + ") REFERENCES " + //FK ingred
                    TABLE_INGREDIENTS + "(" + TIngredients.COLUMN_NOM + "))";

    public static final String TABLE_SUBSTITUTSPARTICIPA = "substitutsparticipa"; //ing substituts participa en recepta
    public static final String SQL_CREATE_ENTRIES4 =
            "CREATE TABLE " + TABLE_SUBSTITUTSPARTICIPA + " (" +
                    TSubstitutsParticipa.COLUMN_RECEPTA +  " " + TEXT_TYPE + COMMA_SEP +     //nom de la recepta
                    TSubstitutsParticipa.COLUMN_INGREDIENT +  " " + TEXT_TYPE + COMMA_SEP +  //ingredient participant
                    TSubstitutsParticipa.COLUMN_SUBSTITUT + " " + TEXT_TYPE + COMMA_SEP +   //ing substitut
                    " PRIMARY KEY (" + TSubstitutsParticipa.COLUMN_RECEPTA + COMMA_SEP +
                    TSubstitutsParticipa.COLUMN_INGREDIENT + COMMA_SEP +
                    TSubstitutsParticipa.COLUMN_SUBSTITUT + " )" + COMMA_SEP + //PK composta
                    "FOREIGN KEY(" + TSubstitutsParticipa.COLUMN_RECEPTA + COMMA_SEP +
                    TSubstitutsParticipa.COLUMN_INGREDIENT + ") REFERENCES " +
                    TABLE_PARTICIPA + "(" + TParticipa.COLUMN_RECEPTA + COMMA_SEP +
                    TParticipa.COLUMN_INGREDIENT + "))";            //FK participa, composta


    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_RECEPTES
            + COMMA_SEP + TABLE_INGREDIENTS + COMMA_SEP + TABLE_PARTICIPA
            + COMMA_SEP + TABLE_SUBSTITUTSPARTICIPA;

    public Contract() {
    }

    public static abstract class TRecepta implements BaseColumns {

        public static final String COLUMN_NOM = "_nom";
        public static final String COLUMN_DESCRIPCIO = "descripcio";
        public static final String COLUMN_IMATGE = "imatge";
        public static final String COLUMN_TIPUS = "tipus";
    }

    public static abstract class TIngredients implements BaseColumns {

        public static final String COLUMN_NOM = "_nom";
        public static final String COLUMN_MESURA = "mesura";
    }

    public static abstract class TParticipa implements BaseColumns {

        public static final String COLUMN_RECEPTA = "_recepta";
        public static final String COLUMN_INGREDIENT = "_ingredient";
        public static final String COLUMN_QUANTITAT = "quantitat";
    }

    public static abstract class TSubstitutsParticipa implements BaseColumns {

        public static final String COLUMN_RECEPTA = "_recepta";
        public static final String COLUMN_INGREDIENT = "_ingredient";
        public static final String COLUMN_SUBSTITUT = "substitut";
    }
}