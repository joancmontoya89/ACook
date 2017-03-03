package montoya.girona.joan.idi.fib.acook;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joangmontoya on 27/11/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     *  Variables privades de la classe
     */
    private final String LOG_TAG = DBHelper.class.getSimpleName();

    // Aplicacio del patro Singleton
    private static DBHelper savedInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cookBookDB.db";

    private ContentResolver myCR;


    /**
     *  Metodes publics
     * @param context
     */

    public static synchronized DBHelper getInstance(Context context) {
        if (savedInstance == null) {
            savedInstance = new DBHelper(context.getApplicationContext());
        }
        return savedInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Cridat quan s'esta configurant la conexio de la base de dades
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.SQL_CREATE_ENTRIES1);
        db.execSQL(Contract.SQL_CREATE_ENTRIES2);
        db.execSQL(Contract.SQL_CREATE_ENTRIES3);
        db.execSQL(Contract.SQL_CREATE_ENTRIES4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_SUBSTITUTSPARTICIPA);
            db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_PARTICIPA);
            db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_INGREDIENTS);
            db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_RECEPTES);

            onCreate(db);
        }
    }

    /*
     *  INSERTS
     */
    public void addRecepta(String nom, String descripcio, String imatge, String tipus) throws SQLiteConstraintException {

        ContentValues values = new ContentValues();
        values.put(Contract.TRecepta.COLUMN_NOM, nom);
        values.put(Contract.TRecepta.COLUMN_DESCRIPCIO, descripcio);
        values.put(Contract.TRecepta.COLUMN_IMATGE, imatge);
        values.put(Contract.TRecepta.COLUMN_TIPUS, tipus);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(Contract.TABLE_RECEPTES, null, values);
        String selectQuery = "SELECT * FROM " + Contract.TABLE_RECEPTES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            Recepta recepta = new Recepta();
            recepta.setNom(cursor.getString(0));
            recepta.setDescripcio(cursor.getString(1));
            recepta.setTipus(cursor.getString(2));
            recepta.setImatge(cursor.getString(3));

            /*
            String res = "Resultat select: nom: " + recepta.getNom() +
                    ", descripcio: " + recepta.getDescripcio() +
                    ", imatge: " + recepta.getImatge() +
                    ", tipus: " + recepta.getTipus() + "\n\n";
            Log.d(LOG_TAG, res);
            */
        }
    }

    public void createIngredient(String nom, String mesura) {
        ContentValues values = new ContentValues();
        values.put(Contract.TIngredients.COLUMN_NOM, nom);
        values.put(Contract.TIngredients.COLUMN_MESURA, mesura);

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.insert(Contract.TABLE_INGREDIENTS, null, values);
        }
        catch (Exception e) {
            String s = "Problema afegint ingredient, ja hi era\n";
            s += "Missatge: " + e.getMessage();
            Log.d(LOG_TAG, s);
        }
    }

    public void createParticipa(String recepta, String ingredient, int quantitat) {
        ContentValues values = new ContentValues();
        values.put(Contract.TParticipa.COLUMN_RECEPTA, recepta);
        values.put(Contract.TParticipa.COLUMN_INGREDIENT, ingredient);
        values.put(Contract.TParticipa.COLUMN_QUANTITAT, quantitat);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(Contract.TABLE_PARTICIPA, null, values);
    }

    public void createSubstitutsParticipa(String recepta, String ingredient, String substitut) {
        ContentValues values = new ContentValues();
        values.put(Contract.TSubstitutsParticipa.COLUMN_RECEPTA, recepta);
        values.put(Contract.TSubstitutsParticipa.COLUMN_INGREDIENT, ingredient);
        values.put(Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT, substitut);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(Contract.TABLE_SUBSTITUTSPARTICIPA, null, values);
    }

    /*
     * UPDATES
     */

    public void updateRecepta(String nom, String descripcio, String imatge, String tipus) {
        ContentValues values = new ContentValues();
        values.put(Contract.TRecepta.COLUMN_DESCRIPCIO, descripcio);
        values.put(Contract.TRecepta.COLUMN_IMATGE, imatge);
        values.put(Contract.TRecepta.COLUMN_TIPUS, tipus);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.TABLE_RECEPTES, values, Contract.TRecepta.COLUMN_NOM + "=" + nom, null);
    }

    public void updateIngredient(String nom, String mesura) {
        ContentValues values = new ContentValues();
        values.put(Contract.TIngredients.COLUMN_MESURA, mesura);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.TABLE_INGREDIENTS, values, Contract.TIngredients.COLUMN_NOM + "=" + nom, null);
    }

    public void updateParticipa(String recepta, String ingredient, double quantitat) {
        String condicio = Contract.TParticipa.COLUMN_RECEPTA + " = '" + recepta + "'";
        condicio += " AND " + Contract.TParticipa.COLUMN_INGREDIENT + " = '" + ingredient + "'";
        ContentValues values = new ContentValues();
        values.put(Contract.TParticipa.COLUMN_QUANTITAT, quantitat);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.TABLE_PARTICIPA, values, condicio, null);
    }

    public void updateSubstitutsParticipa(String recepta, String ingredient, String substitut) {
        String condicio = Contract.TSubstitutsParticipa.COLUMN_RECEPTA + " = '" + recepta + "'";
        condicio += " AND " + Contract.TSubstitutsParticipa.COLUMN_INGREDIENT + " = '" + ingredient + "'";
        ContentValues values = new ContentValues();
        values.put(Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT, substitut);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Contract.TABLE_SUBSTITUTSPARTICIPA, values, condicio, null);
    }

    /*
     * SELECTS
     */

    public Cursor select_recepta() {
        String[] cols = new String [] {Contract.TRecepta.COLUMN_NOM,
                Contract.TRecepta.COLUMN_DESCRIPCIO,
                Contract.TRecepta.COLUMN_IMATGE,
                Contract.TRecepta.COLUMN_TIPUS};
        String ORDER_BY = Contract.TRecepta.COLUMN_NOM + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, Contract.TABLE_RECEPTES, cols,
                null, null, null, null, ORDER_BY, null);
        return cursor;
    }

    public Cursor select_recepta(String nom_recepta) {
        String query = "Select * FROM " + Contract.TABLE_RECEPTES + " WHERE " +
                Contract.TRecepta.COLUMN_NOM + " =  '" + nom_recepta + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor select_receptes_per_tipus(String tipus_cuina) {
        String query = "Select * FROM " + Contract.TABLE_RECEPTES + " WHERE " +
                Contract.TRecepta.COLUMN_TIPUS + " =  '" + tipus_cuina +
                "' ORDER BY '" + Contract.TRecepta.COLUMN_NOM + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor selectIngredients() {
        String[] cols = new String [] {Contract.TIngredients.COLUMN_NOM,
                Contract.TIngredients.COLUMN_MESURA};
        String ORDER_BY = Contract.TIngredients.COLUMN_NOM + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, Contract.TABLE_INGREDIENTS, cols,
                null, null, null, null, ORDER_BY, null);
        return cursor;
    }

    public String select_mesura_ingredient(String nom_i) {
        String[] cols = new String [] {Contract.TIngredients.COLUMN_NOM,
                Contract.TIngredients.COLUMN_MESURA};
        SQLiteDatabase db = this.getReadableDatabase();
        String condicio = Contract.TIngredients.COLUMN_NOM + " = '" + nom_i + "'";
        Cursor cursor = db.query(true, Contract.TABLE_INGREDIENTS, cols,
                condicio, null, null, null, null, null);
         String res = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            res = cursor.getString(cursor.getColumnIndex(
                    Contract.TIngredients.COLUMN_MESURA));
        }
        return res;
    }

    public Cursor selectParticipa() {
        String[] cols = new String [] {Contract.TParticipa.COLUMN_RECEPTA,
                Contract.TParticipa.COLUMN_INGREDIENT,
                Contract.TParticipa.COLUMN_QUANTITAT};
        String ORDER_BY = Contract.TParticipa.COLUMN_RECEPTA + "," +
                Contract.TParticipa.COLUMN_INGREDIENT + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, Contract.TABLE_PARTICIPA, cols,
                null, null, null, null, ORDER_BY, null);
        return cursor;
    }

    /*
     * ingredients que participen en una recepta determinada
     */
    public Cursor selectParticipa(String nom_r) {
        String query = "Select * FROM " + Contract.TABLE_PARTICIPA +
                " WHERE " + Contract.TParticipa.COLUMN_RECEPTA +
                " = " + "'" + nom_r + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String res = "Resultat: \n";
            while (cursor.moveToNext()) {
                res += "(" + cursor.getString(cursor.getColumnIndex(
                                Contract.TParticipa.COLUMN_RECEPTA)) + " , " +
                        cursor.getString(cursor.getColumnIndex(
                                Contract.TParticipa.COLUMN_INGREDIENT)) +
                        " , " + cursor.getString(cursor.getColumnIndex(
                        Contract.TParticipa.COLUMN_QUANTITAT)) + " )\n";
            }
            Log.d(LOG_TAG, res);
        }
        return cursor;
    }

    public Cursor select_substituts_participa() {
        String[] cols = new String [] {Contract.TSubstitutsParticipa.COLUMN_RECEPTA,
                Contract.TSubstitutsParticipa.COLUMN_INGREDIENT,
                Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT};
        String ORDER_BY = Contract.TSubstitutsParticipa.COLUMN_RECEPTA + "," +
                Contract.TSubstitutsParticipa.COLUMN_INGREDIENT + "," +
                Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, Contract.TABLE_SUBSTITUTSPARTICIPA, cols,
                null, null, null, null, ORDER_BY, null);
        return cursor;
    }

    // retorna tots substituts participants en recepta com a subs d ingredient
    public Cursor select_substituts_participa(String nom_r, String nom_i) {
        String[] cols = new String [] {Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT,};
        String ORDER_BY = Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT +
                " ASC";
        String condicio = Contract.TSubstitutsParticipa.COLUMN_RECEPTA + " = '" +
                nom_r + "' AND " + Contract.TSubstitutsParticipa.COLUMN_INGREDIENT +
                " = '" + nom_i + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, Contract.TABLE_SUBSTITUTSPARTICIPA, cols,
                condicio, null, null, null, ORDER_BY, null);
        return cursor;
    }

    public Cursor select_tipus_diferents() {
        String query = "Select DISTINCT " + Contract.TRecepta.COLUMN_TIPUS +
                " FROM " + Contract.TABLE_RECEPTES + " ORDER BY " +
                Contract.TRecepta.COLUMN_TIPUS + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }


    /**
     * DELETE actions: metodes per eliminar
     */
    public void delete_recepta(String nom_r) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = new String [] {Contract.TParticipa.COLUMN_RECEPTA,
                Contract.TParticipa.COLUMN_INGREDIENT};
        String condicio = Contract.TParticipa.COLUMN_RECEPTA + " = '" + nom_r + "'";
        Cursor cursor = db.query(true, Contract.TABLE_PARTICIPA, cols,
                condicio, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String nom_i = cursor.getString(cursor.getColumnIndex(
                        Contract.TParticipa.COLUMN_INGREDIENT));
                Log.d(LOG_TAG, "rec: " + nom_r + "\ning: " + nom_i);
                this.delete_participa(nom_r, nom_i);
            }
            while (cursor.moveToNext());
        }

        String query = "DELETE FROM " + Contract.TABLE_RECEPTES + " WHERE " +
                Contract.TRecepta.COLUMN_NOM + " = '" + nom_r + "'";
        db.execSQL(query);
    }

    public void delete_ingredient(String nom_i) {      //per a futures ampliacions
        SQLiteDatabase db = this.getWritableDatabase();
        String[] cols = new String [] {Contract.TParticipa.COLUMN_RECEPTA};
        String condicio = Contract.TParticipa.COLUMN_INGREDIENT +
                " = '" + nom_i + "'";
        Cursor cursor_receptes = db.query(true, Contract.TABLE_PARTICIPA, cols,
                condicio, null, null, null, null, null);
        if (cursor_receptes != null && cursor_receptes.getCount() > 0) {
            cursor_receptes.moveToFirst();
            while (cursor_receptes.moveToNext()) {
                String nom_r = cursor_receptes.getString(cursor_receptes.getColumnIndex(
                        Contract.TParticipa.COLUMN_RECEPTA));
                this.delete_participa(nom_r, nom_i);
            }
        }
        String delete_query = "DELETE FROM " + Contract.TABLE_INGREDIENTS + " " +
                "WHERE " + Contract.TIngredients.COLUMN_NOM + " = '" + nom_i + "'";
        db.execSQL(delete_query);
    }

    public void delete_participa(String nom_r, String nom_i) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = this.select_substituts_participa(nom_r, nom_i);
        if (cursor != null && cursor.getCount() > 0) {
            String query_del = "DELETE FROM " + Contract.TABLE_SUBSTITUTSPARTICIPA +
                    " WHERE " + Contract.TSubstitutsParticipa.COLUMN_RECEPTA + " = '" +
                    nom_r + "' AND " + Contract.TSubstitutsParticipa.COLUMN_INGREDIENT +
                    " = '" + nom_i + "'";
            db.execSQL(query_del);
        }
        String query = "DELETE FROM " + Contract.TABLE_PARTICIPA + " WHERE " +
                Contract.TParticipa.COLUMN_RECEPTA + " = '" + nom_r + "' AND " +
                Contract.TParticipa.COLUMN_INGREDIENT + " = '" + nom_i + "'";
        db.execSQL(query);
    }

    public void delete_substitut_participa(String nom_r, String nom_i, String nom_s) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + Contract.TABLE_SUBSTITUTSPARTICIPA +
                " WHERE " + Contract.TSubstitutsParticipa.COLUMN_RECEPTA + " = '" +
                nom_r + "' AND " + Contract.TSubstitutsParticipa.COLUMN_INGREDIENT +
                " = '" + nom_i + "' AND " + Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT +
                " = '" + nom_s + "'";
        db.execSQL(query);
        db.close();
    }
}
