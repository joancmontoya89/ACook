package montoya.girona.joan.idi.fib.acook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.Toast;

import java.util.ArrayList;

public class Recepta extends ActionBarActivity {

    /*
         * Atributs de la classe
         */
    private final String LOG_TAG = Recepta.class.getSimpleName();

    private String nom;                             // nom de la recepta
    private ArrayList<String> ingredients;          // ingredients de la recepta
    private ArrayList<String> substituts;           // per a substituts d'ingred
    private String descripcio;                      // descripcio de la recepta
    private String imatge;                          // ruta de la imatge
    private String tipus;                           // tipus de cuina a que pertany
    private int pos_lv_ingredients;                 // posicio seleccionada lv

    private TextView tvNom;
    private TextView tvDescripcio;
    private TextView tvTipus;
    private ListView lvIngredients;
    private ListView lvSubstituts;
    private ImageView ivRecepta;
    private TextView tvTitolDescripcio;
    private TextView tvTitolTipus;
    private TextView tvTitolIngredients;
    private TextView tvTitolSubstituts;

    /*
     * Funcions publiques de la classe
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepta);

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            Log.e(LOG_TAG, "Error en la introduccio d'extres");
            return;
        }

        nom = extras.getString("recepta");

        inicialitzar_atributs();
        get_dades_bd();
        carrega_dades();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {   //boto back
            onBackPressed();
        }
        return true;
    }

    // getters and setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getImatge() {
        return imatge;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public void onClickAddRecepta(View view) {
        Intent intent = new Intent(this, Afegir_Recepta.class);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            String s = "Error al canviar a la Recepta per afegir-ne una";
            Log.e(LOG_TAG, s);
        }
    }

    public void onClickEliminarRecepta(View view) {
        DBHelper dbHelper = DBHelper.getInstance(this);
        dbHelper.delete_recepta(nom);
        Toast toast = Toast.makeText(this, "Recepta eliminada correctament",
                Toast.LENGTH_SHORT);
        toast.show();
        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_OK, returnIntent);
        returnIntent.putExtra("result", "result");
        finish();
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inici, menu);

        //add MenuItem(s) to ActionBar using Java code
        MenuItem menuItem_eliminar = menu.add(0, R.id.menuid_eliminar, 0, "Supr.");
        menuItem_eliminar.setIcon(android.R.drawable.ic_input_delete);
        MenuItemCompat.setShowAsAction(menuItem_eliminar,
                MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    /*
     * Metode per editar la recepta
     */
    public void onClickEditarRecepta(View view) {
        Intent intent = new Intent(this, Afegir_Recepta.class);
        intent.putExtra("recepta", nom);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Intent returnIntent = getIntent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            onBackPressed();
        }
    }

    /**
     * Funcions privades
     */
    /*
     * Metode per inicialitzar els diferents atributs de la classe
     */
    private void inicialitzar_atributs() {
        //lliguem els widgets del layout
        tvNom = (TextView)findViewById(R.id.tvNomRecepta);
        tvDescripcio = (TextView)findViewById(R.id.tvDescripcioRecepta);
        tvTipus = (TextView)findViewById(R.id.tvTipusCuinaRecepta);
        lvIngredients = (ListView)findViewById(R.id.lvIngredientsAfegitsRecepta);
        lvSubstituts = (ListView)findViewById(R.id.lvSubstitutsAfegitsRecepta);
        ivRecepta = (ImageView)findViewById(R.id.imgRecepta);
        tvTitolDescripcio = (TextView)findViewById(R.id.tvTitolDescripcioRecepta);
        tvTitolTipus = (TextView)findViewById(R.id.tvTitolTipusCuinaRecepta);
        tvTitolIngredients = (TextView)findViewById(R.id.tvTitolIngredientsAfegitsRecepta);
        tvTitolSubstituts = (TextView)findViewById(R.id.tvTitolSubstitutsRecepta);

        //inicialitzem els atributs privats de la classe
        ingredients = new ArrayList<>();
        substituts = new ArrayList<>();
        pos_lv_ingredients = -1;

        lvIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int pos = i;
                lvIngredients.setSelection(pos);
                pos_lv_ingredients = pos;
                String s = "i = " + i + "\n" + "pos_lv_ingredients = " + pos_lv_ingredients;
                s += "\n getSelected... = " + lvIngredients.getSelectedItemPosition() + "\n";
                Log.d(LOG_TAG, s);
            }
        });
    }

    /*
     * Metode per agafar les dades necessaries de la base de dades
     */
    private void get_dades_bd() {
        DBHelper dbHelper;
        dbHelper = DBHelper.getInstance(this);

        //obtenim descripcio, imatge i tipus de la recepta
        Cursor cursor_recepta = dbHelper.select_recepta(nom);
        if (cursor_recepta != null && cursor_recepta.getCount() > 0) {
            cursor_recepta.moveToFirst();
            descripcio = cursor_recepta.getString(cursor_recepta.getColumnIndex(
                    Contract.TRecepta.COLUMN_DESCRIPCIO));
            imatge = cursor_recepta.getString(cursor_recepta.getColumnIndex(
                    Contract.TRecepta.COLUMN_IMATGE));
            tipus = cursor_recepta.getString(cursor_recepta.getColumnIndex(
                    Contract.TRecepta.COLUMN_TIPUS));
        }

        //obtenim ingredients de la recepta
        Cursor cursor_participa = dbHelper.selectParticipa(nom);
        if (cursor_participa != null && cursor_participa.getCount() > 0) {
            cursor_participa.moveToFirst();
            while (cursor_participa.moveToNext()) {
                String ing = cursor_participa.getString(cursor_participa.getColumnIndex(
                        Contract.TParticipa.COLUMN_INGREDIENT));
                String s_aux = ing;
                ing += ": " + cursor_participa.getString(cursor_participa.getColumnIndex(
                        Contract.TParticipa.COLUMN_QUANTITAT)) + " ";
                ing += dbHelper.select_mesura_ingredient(s_aux);
                ingredients.add(ing);
            }
        }

        //obtenim els substituts per a cada ingredient de la recepta
        int n = ingredients.size();
        for (int i = 0; i < n; i++) {
            String ing = ingredients.get(i);
            Cursor cursor_substituts = dbHelper.select_substituts_participa(nom, ing);
            //ArrayList<String> subs_aux = new ArrayList<>();
            if (cursor_substituts != null && cursor_substituts.getCount() > 0) {
                String s = ingredients.get(i) + ": ";
                s += cursor_substituts.getString(cursor_substituts.getColumnIndex(
                        Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT));
                //subs_aux.add(s);
                substituts.add(s);
            }
            //substituts.add(subs_aux);
        }
    }

    /*
     * Metode per omplir els diferents widgets de la nostra vista
     */
    private void carrega_dades() {
        tvNom.setText(nom);
        if (descripcio.contentEquals("")) {
            tvTitolDescripcio.setVisibility(View.GONE);
            tvDescripcio.setVisibility(View.GONE);
        }
        else tvDescripcio.setText(descripcio);

        if (tipus.contentEquals("")) {
            tvTitolTipus.setVisibility(View.GONE);
            tvTipus.setVisibility(View.GONE);
        }
        else tvTipus.setText(tipus);

        //carreguem imatge
        Drawable d;
        if (imatge.contentEquals("-")) {
            d = getResources().getDrawable(R.drawable.food);
        }
        else {
            Bitmap imgRecepta = BitmapFactory.decodeFile(imatge);
            d = new BitmapDrawable(imgRecepta);
        }
        ivRecepta.setBackground(d);

        if (ingredients == null || ingredients.isEmpty()) {
            tvTitolIngredients.setVisibility(View.GONE);
            lvIngredients.setVisibility(View.GONE);
        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.support_simple_spinner_dropdown_item,
                    ingredients);
            lvIngredients.setAdapter(adapter);
        }

        if (substituts == null || substituts.isEmpty()) {
            tvTitolSubstituts.setVisibility(View.GONE);
            lvSubstituts.setVisibility(View.GONE);
        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.support_simple_spinner_dropdown_item,
                    substituts);
            lvSubstituts.setAdapter(adapter);
        }
    }

}
