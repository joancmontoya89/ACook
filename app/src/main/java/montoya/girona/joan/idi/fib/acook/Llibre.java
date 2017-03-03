package montoya.girona.joan.idi.fib.acook;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Llibre extends ActionBarActivity {

    /**
     *  Atributs privats de la classe Llibre
     */

    private final String LOG_TAG = Llibre.class.getSimpleName();
    private ArrayList<ReceptesEntrada> imgs_noms_receptes;
    private ArrayList<String> tipus_menjar;

    private ListView lvReceptes;
    private ListView lvTipus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llibre);

        afegir_ingredients_bd();

        inicialitzar_atributs_classe();
        get_dades_bd();

        //Log.d(LOG_TAG, "Dades tipus: " + tipus_menjar);
        //Log.d(LOG_TAG, "Noms receptes: " + noms_receptes);

        carregar_lv();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inici, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public void onClickAddRecepta(View view) {
        Intent intent = new Intent(this, Afegir_Recepta.class);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 0);
            inicialitzar_atributs_classe();
            get_dades_bd();     //actualitzem per si hi ha una nova recepta o mod
            carregar_lv();
        }
        else {
            Log.e(LOG_TAG, "Error al canviar a la Recepta per afegir-ne una");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        inicialitzar_atributs_classe();
        get_dades_bd();     //actualitzem per si hi ha una nova recepta o mod
        carregar_lv();
    }

    public void onClickTipusCuina(View view) {
        Intent intent = new Intent(this, Recepta.class);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Log.e(LOG_TAG, "Error al canviar a TipusCuina");
        }
    }

    /**
     * Metodes privats de la classe
     */
    private void afegir_ingredients_bd() {
        ArrayList<String> nom_ingredients = new ArrayList<>();
        ArrayList<String> mes_ingredients = new ArrayList<>();  // mesura
        DBHelper dbHelper = DBHelper.getInstance(this);
        Cursor cursor = dbHelper.selectIngredients();
        if (cursor == null || cursor.getCount() == 0) {
            String m = "ml.";
            String g = "grams";
            String u = "unitat";
            String c = "cullaradeta";

            nom_ingredients.add("All");
            mes_ingredients.add(u);

            nom_ingredients.add("Arròs");
            mes_ingredients.add(g);

            nom_ingredients.add("Canyella");
            mes_ingredients.add(c);

            nom_ingredients.add("Carn de pollastre");
            mes_ingredients.add(g);

            nom_ingredients.add("Carn de porc");
            mes_ingredients.add(g);

            nom_ingredients.add("Carn de vedella");
            mes_ingredients.add(g);

            nom_ingredients.add("Ceba");
            mes_ingredients.add(g);

            nom_ingredients.add("Cigrons");
            mes_ingredients.add(g);

            nom_ingredients.add("Enciam");
            mes_ingredients.add(g);

            nom_ingredients.add("Espaguetis");
            mes_ingredients.add(g);

            nom_ingredients.add("Espinacs");
            mes_ingredients.add(g);

            nom_ingredients.add("Farina");
            mes_ingredients.add(g);

            nom_ingredients.add("Formatge");
            mes_ingredients.add(g);

            nom_ingredients.add("Iogurt");
            mes_ingredients.add(u);

            nom_ingredients.add("Llet");
            mes_ingredients.add(m);

            nom_ingredients.add("Llevadura de reposteria");
            mes_ingredients.add(g);

            nom_ingredients.add("Llimona");
            mes_ingredients.add(u);

            nom_ingredients.add("Macarrons");
            mes_ingredients.add(g);

            nom_ingredients.add("Mantega");
            mes_ingredients.add(g);

            nom_ingredients.add("Nata liquida");
            mes_ingredients.add(m);

            nom_ingredients.add("Oli de oliva");
            mes_ingredients.add(m);

            nom_ingredients.add("Ous");
            mes_ingredients.add(u);

            nom_ingredients.add("Patata");
            mes_ingredients.add(g);

            nom_ingredients.add("Pernil");
            mes_ingredients.add(g);

            nom_ingredients.add("Sal");
            mes_ingredients.add(g);

            nom_ingredients.add("Sucre");
            mes_ingredients.add(c);

            nom_ingredients.add("Taronja");
            mes_ingredients.add(u);

            nom_ingredients.add("Tomàquet");
            mes_ingredients.add(u);

            nom_ingredients.add("Tonyina en llauna");
            mes_ingredients.add(u);

            nom_ingredients.add("ZZZZZZ");
            mes_ingredients.add(u);

            int n = nom_ingredients.size();
            for (int i = 0; i < n; i++) {
                dbHelper.createIngredient(nom_ingredients.get(i), mes_ingredients.get(i));
            }

            //afegim 5 receptes
            dbHelper.addRecepta("Ou ferrat", "Preparar amb l oli ben calent i anar posant oli a sobre",
                    "-", "Cuina tradicional");
            dbHelper.createParticipa("Ou ferrat", "Ous", 2);
            dbHelper.createParticipa("Ou ferrat", "Oli de oliva", 20);

            dbHelper.addRecepta("Arros bullit", "Arros bullit al estil tradicional de sempre",
                    "-", "Cuina tradicional");
            dbHelper.createParticipa("Arros bullit", "All", 3);
            dbHelper.createParticipa("Arros bullit", "Arròs", 100);
            dbHelper.createParticipa("Arros bullit", "Oli de oliva", 20);

            dbHelper.addRecepta("Truita francesa", "Preparar amb l oli ben calent i els ous batuts",
                    "-", "Cuina francesa");
            dbHelper.createParticipa("Truita francesa", "Ous", 2);
            dbHelper.createParticipa("Truita francesa", "Oli de oliva", 10);
            dbHelper.createSubstitutsParticipa("Truita francesa", "Oli de oliva", "Mantega");

            dbHelper.addRecepta("Filet de vedella a la planxa", "Posar un raig de oli a la planxa i esperar a que s escalfi be",
                    "-", "Cuina per solters");
            dbHelper.createParticipa("Filet de vedella a la planxa", "Carn de vedella", 100);
            dbHelper.createParticipa("Filet de vedella a la planxa", "Oli de oliva", 3);
            dbHelper.createParticipa("Filet de vedella a la planxa", "Sal", 1);

            dbHelper.addRecepta("Suc de taronja", "Expremer les taronges a el expremedor i afegir-hi el sucre",
                    "-", "Natural");
            dbHelper.createParticipa("Suc de taronja", "Carn de vedella", 2);
            dbHelper.createParticipa("Suc de taronja", "Sucre", 3);

        }
        //cursor.close();
        dbHelper.close();
    }

    private void inicialitzar_atributs_classe() {
        imgs_noms_receptes = new ArrayList<ReceptesEntrada>();
        tipus_menjar = new ArrayList<String>();

        //lliguem widgets amb layout
        lvReceptes = (ListView)findViewById(R.id.lvReceptesInici);
        lvTipus = (ListView)findViewById(R.id.lvTipusInici);

        lvReceptes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = i;
                String nom_r = imgs_noms_receptes.get(pos).getNom_recepta();
                Intent intent = new Intent(Llibre.this, Recepta.class);
                String recepta = "recepta";
                intent.putExtra(recepta, nom_r);
                startActivity(intent);
            }
        });
    }

    private void get_dades_bd() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        Cursor cursor_receptes = dbHelper.select_recepta();
        if ((cursor_receptes != null) && cursor_receptes.getCount() > 0) {
            cursor_receptes.moveToFirst();
            while (!cursor_receptes.isAfterLast()) {
                String nom = cursor_receptes.getString(cursor_receptes.getColumnIndex(
                        Contract.TRecepta.COLUMN_NOM));
                String img = cursor_receptes.getString(cursor_receptes.getColumnIndex(
                        Contract.TRecepta.COLUMN_IMATGE));
                ReceptesEntrada re = new ReceptesEntrada(nom, img);
                imgs_noms_receptes.add(re);
                cursor_receptes.moveToNext();
            }
        }
        else {
            //inserim les receptes inicials
        }
        Cursor cursor_tipus = dbHelper.select_tipus_diferents();
        if ((cursor_tipus != null) && (cursor_tipus.getCount() > 0)) {
            cursor_tipus.moveToFirst();
            while (!cursor_tipus.isAfterLast()) {
                String tipus = cursor_tipus.getString(cursor_tipus.getColumnIndex(
                        Contract.TRecepta.COLUMN_TIPUS));
                tipus_menjar.add(tipus);
                cursor_tipus.moveToNext();
            }
        }
        else {
            //tipus_menjar.add("-");
        }
        dbHelper.close();
    }

    /*
     *
     */
    private void carregar_lv() {
        lvReceptes.setAdapter(new CustomAdapter(this, imgs_noms_receptes) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView tvNom_recepta = (TextView) view.findViewById(R.id.nomReceptaLlibre);
                    if (tvNom_recepta != null)
                        tvNom_recepta.setText(((ReceptesEntrada)entrada).getNom_recepta());

                    ImageView ivRecepta = (ImageView) view.findViewById(R.id.ivReceptaLlibre);
                    if (ivRecepta != null) {
                        //ivRecepta.setImageResource(((Lista_entrada) entrada).get_idImagen());

                        String img = ((ReceptesEntrada)entrada).getImg_recepta();
                        Drawable d;
                        if (img.contentEquals("-")) {
                            d = getResources().getDrawable(R.drawable.food);
                        }
                        else {
                            Bitmap imgRecepta = BitmapFactory.decodeFile(img);
                            d = new BitmapDrawable(imgRecepta);
                        }
                        ivRecepta.setBackground(d);
                    }
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                tipus_menjar);
        lvTipus.setAdapter(adapter);
    }

}
