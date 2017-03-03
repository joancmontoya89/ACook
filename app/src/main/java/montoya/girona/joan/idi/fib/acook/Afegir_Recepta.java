package montoya.girona.joan.idi.fib.acook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Afegir_Recepta extends ActionBarActivity {

    /*
     * Atributs de la classe
     */
    private final String LOG_TAG = Afegir_Recepta.class.getSimpleName();

    private String nom;             // nom de la recepta
    private String descripcio;      // descripcio de la recepta
    private String ingredient;      // ingredient seleccionat de l'spinner
    private String mesura;          // mesura de l'ingredient (g, ml, unitat...)
    private int quantitat;          // quantitat de l'ingredient
    private int scrollY;            // posicio de l'scroll vertical
    private String imatge;          // ruta de la imatge
    private String tipus;           // tipus de cuina a que pertany
    private int pos_lv_ingredients;
    private int pos_lv_substituts;
    private int lv_ing_o_lv_sub;            //0 -> lvIng, 1 -> lvSubs seleccionat
                                            //-1 -> cap
    private ArrayList<String> ingredients;  //de la llista de la bd
    private ArrayList<String> mesures;      //ingredients bd (ml, g, unitat)
    private ArrayList<Integer> quantitats;  //qtts dels ingredients de la recepta
    private ArrayList<String> ingredientsArrayList; //AL<ing+mesura> per spIngredients
    private ArrayList<String> mesuresAfegits;       //mesures afegides a lv
    private ArrayList<String> ingAfegits;   //ing afegits a lvIngredients a-z
    private ArrayList<String> infoIngAfegits;   //^ing + qtt + mesura a-z per lvIngredients
    private ArrayList<SubstitutParticipa> subsPartArrayList;    //ArrL<nomR, nomI, subs>

    private EditText etNom;
    private EditText etDescripcio;
    private ImageView ivImatge;
    private EditText etTipus;
    private Spinner spIngredients;
    private ListView lvIngredients;
    private EditText etQuantitat;
    private EditText etSubstitut;
    private ListView lvSubstituts;
    private ScrollView scrollView;
    private TextView tvTitol;

    private static final int SELECTED_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir__recepta);

        // Lligo els diferents widgets ...
        etNom = (EditText)findViewById(R.id.etNomAfegirRecepta);
        etDescripcio = (EditText)findViewById(R.id.etDescripcioAfegirRecepta);
        etTipus = (EditText)findViewById(R.id.etTipusCuinaAfegirRecepta);
        ivImatge = (ImageView)findViewById(R.id.imgAfegirRecepta);
        spIngredients = (Spinner)findViewById(R.id.spIngredients);
        lvIngredients = (ListView)findViewById(R.id.lvIngredientsAfegitsAfegirRecepta);
        etQuantitat = (EditText)findViewById(R.id.etQuantitatAfegirRecepta);
        etSubstitut = (EditText)findViewById(R.id.etSubstituts);
        lvSubstituts = (ListView)findViewById(R.id.lvSubstitutsAfegitsAfegirRecepta);
        scrollView = (ScrollView)findViewById(R.id.svAfegirRecepta);
        tvTitol = (TextView)findViewById(R.id.tvTitolAfegirRecepta);
        lv_ing_o_lv_sub = -1;
        subsPartArrayList = new ArrayList<SubstitutParticipa>();

        spIngredients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    ingredient = ingredients.get(i);
                }
                lv_ing_o_lv_sub = -1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        // Inicialitzo ingAfegits
        ingAfegits = new ArrayList<String>();
        quantitats = new ArrayList<Integer>();
        mesuresAfegits = new ArrayList<String>();

        // Omplo l'spinner
        carregar_spinner();

        lvIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = lvIngredients.getSelectedItemPosition();
                lvIngredients.setSelection(pos);
                ingredient = ingAfegits.get(position);
                pos_lv_ingredients = position;
                lv_ing_o_lv_sub = 0;
                carregar_lv_substitut_participa();
            }
        });

        lvSubstituts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = lvSubstituts.getSelectedItemPosition();
                lvSubstituts.setSelection(pos);
                pos_lv_substituts = position;
                lv_ing_o_lv_sub = 1;
            }
        });

        registerForContextMenu(lvIngredients);      //per poder desplegar el contextual menu
        registerForContextMenu(lvSubstituts);

        //comprobo si el que vull fer es editar una recepta ja existent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            if (bundle.getString("recepta") != null) {
                nom = bundle.getString("recepta");
                tvTitol.setText("Editar recepta");
                get_dades_bd();             //agafem les dades de bd i carreguem
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_afegir_recepta, menu);

        //add MenuItem(s) to ActionBar using Java code
        MenuItem menuItem_eliminar = menu.add(0, R.id.menuid_eliminar, 0, "Supr.");
        menuItem_eliminar.setIcon(android.R.drawable.ic_input_delete);
        MenuItemCompat.setShowAsAction(menuItem_eliminar,
                MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuid_eliminar) {
            Log.d(LOG_TAG, "Entro a onOptionsItemSelected");
            if ((lv_ing_o_lv_sub == 0 && pos_lv_ingredients >= 0) ||
                    (lv_ing_o_lv_sub == 1 && pos_lv_substituts >= 0)) {
                eliminar_item_seleccionat_lv();

                if (pos_lv_substituts == -1 && pos_lv_ingredients >= 0) {
                    lvIngredients.setSelection(pos_lv_ingredients);
                }
            }
        }
        else if (item.getItemId() == android.R.id.home) {   //boto back
            onBackPressed();
        }
        return true;
    }

    /**
     * Sobreescrivim el metode per a inflar el contextual menu de la listview
     * d'ingredients
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.afegir_recepta_contextual_menu, menu);
        if (v.getId() == R.id.lvIngredientsAfegitsAfegirRecepta) {
            lv_ing_o_lv_sub = 0;
        }
        else if (v.getId() == R.id.lvSubstitutsAfegitsAfegirRecepta) {
            lv_ing_o_lv_sub = 1;
        }
    }

    /**
     * Metode que diu que fer en cada opcio premuda del contextual menu
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        if (item.getItemId() == R.id.ctxmenuid_eliminar) {
            if (lv_ing_o_lv_sub == 0)
                pos_lv_ingredients = info.position;
            else if (lv_ing_o_lv_sub == 1) {
                pos_lv_substituts = info.position;
            }
            eliminar_item_seleccionat_lv();
        }
        return super.onContextItemSelected(item);
    }

    /**
     *  Accio de premer el boto d'afegir imatge
     * @param v
     */
    public void onClickAddImatge (View v) {
        scrollY = scrollView.getScrollY();
        Log.d(LOG_TAG, "Al clicar scrollY = " + scrollY);
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECTED_PICTURE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            //Log.d(LOG_TAG, "FilePath:   " + filePath);
            imatge = filePath;
            Bitmap imgRecepta = BitmapFactory.decodeFile(filePath);
            //Log.d(LOG_TAG, "yourSelectedImage:   " + imgRecepta);
            Drawable d = new BitmapDrawable(imgRecepta);
            //Log.d(LOG_TAG, "Drawable   OK");
            ivImatge.setBackground(d);
        }
        //Log.d(LOG_TAG, "Abans de fer setScrollY, = " + scrollY);
        scrollY = scrollView.getBottom();
        int scrollX = scrollView.getScrollX();
        scrollView.scrollTo(scrollX, scrollY);
        //scrollView.computeScroll();
        //Log.d(LOG_TAG, "Al retornar, scrollY = " + scrollY);
    }


    /**
     *  Accio de desar recepta
     * @param v
     */
    public void onClickSaveRecepta (View v) {
        nom = etNom.getText().toString();
        descripcio = etDescripcio.getText().toString();
        tipus = etTipus.getText().toString();
        if (nom.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Nom recepta obligatori", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        else {
            try {
                if (imatge == null)
                    imatge = "-";
                DBHelper dbHelper = DBHelper.getInstance(this);
                dbHelper.addRecepta(nom, descripcio, imatge, tipus);    //afegim recepta

                int n = ingAfegits.size();
                for (int i = 0; i < n; i++) {
                    String ing;
                    int qtt;
                    ing = ingAfegits.get(i);
                    Log.d(LOG_TAG, "ingredient a afegir: " + ing);
                    qtt = quantitats.get(i).intValue();
                    dbHelper.createParticipa(nom, ing, qtt);    //afegim ingredient participa en recepta
                }

                // desem SubstitutParticipa a la bd
                n = subsPartArrayList.size();
                //Log.d(LOG_TAG, "Pas 1: n = " + n);
                for (int i = 0; i < n; i++) {
                    SubstitutParticipa subspar = subsPartArrayList.get(i);
                    String nom_r = subspar.getNom_recepta();
                    String nom_i = subspar.getNom_ingredient();
                    String nom_s = subspar.getSubstitut();
                    dbHelper.createSubstitutsParticipa(nom_r, nom_i, nom_s);
                }

                dbHelper.close();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Recepta afegida", Toast.LENGTH_SHORT);
                toast.show();
                Intent returnIntent = getIntent();
                returnIntent.putExtra("result", "result");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                onBackPressed();
            }
            catch (Exception e) {
                Log.d(LOG_TAG, "jaExisteixRecepta: " +
                        e.getMessage());
                finish();
                onBackPressed();
                return;
                //Toast toast = Toast.makeText(getApplicationContext(),
                //        "Recepta existent", Toast.LENGTH_SHORT);
                //toast.show();
            }
        }
    }

    /**
     * Accio que s'executa al premer boto afegir ingredient a la recepta
     * ingredients{-, i1, ..., in}, mesures{-, m1, ..., mn} quantitats{q1, ..., qn}
      */
    public void onClickAfegirIngredient(View view) {
        try {
            if (!ingredient.contentEquals("")) {
                //System.out.println("ingr: " + ingredient);
                if (!ingAfegits.contains(ingredient)) {
                    String ingaux = ingredient;
                    ingAfegits.add(ingaux);
                    String s = etQuantitat.getText().toString();
                    Integer qttaux = new Integer(s);
                    quantitat = qttaux;
                    if (qttaux.intValue() <= 0) {
                        toast_quantitat_no_valida();
                        etQuantitat.setText("");
                        return;
                    }
                    quantitats.add(qttaux);
                    int pos = spIngredients.getSelectedItemPosition();
                    --pos;
                    mesura = mesures.get(pos + 1);
                    Log.d(LOG_TAG, "pos = " + pos + "\n mesura = " + mesura);
                    mesuresAfegits.add(mesura);
                    ordenar_ingredient_afegit();
                    fusionar_info_ing_afegits();
                    //Log.d(LOG_TAG, "Afegint ingredient ..2");
                    lvIngredients.setAdapter(new ArrayAdapter<String>(this,
                            R.layout.support_simple_spinner_dropdown_item,
                            infoIngAfegits));
                    Log.d(LOG_TAG, "Afegint ingredient ..3");
                    etQuantitat.setText("");
                    spIngredients.setSelection(0);
                }
                else if (spIngredients.getSelectedItemPosition() > 0) {      //modificar mitjancant spinner
                    String ingaux = ingredient;
                    //ingAfegits.remove(ingaux);
                    String s = etQuantitat.getText().toString();
                    Integer qttaux = new Integer(s);
                    quantitat = qttaux;
                    if (qttaux.intValue() <= 0) {
                        toast_quantitat_no_valida();
                        etQuantitat.setText("");
                        return;
                    }
                    int pos = ingAfegits.indexOf(ingredient);
                    quantitats.set(pos, qttaux);
                    //mesura = mesures.get(pos + 1);
                    //Log.d(LOG_TAG, "pos = " + pos + "\n mesura = " + mesura);
                    //mesuresAfegits.remove(mesura);
                    ordenar_ingredient_afegit();
                    fusionar_info_ing_afegits();
                    lvIngredients.setAdapter(new ArrayAdapter<String>(this,
                            R.layout.support_simple_spinner_dropdown_item,
                            infoIngAfegits));
                    etQuantitat.setText("");
                    spIngredients.setSelection(0);
                }
                else if (pos_lv_ingredients >= 0) {          //volem modificar nomes la quantitat
                    Log.d(LOG_TAG, "lvIngredients esta seleccionat");
                    String ingaux = ingAfegits.get(pos_lv_ingredients);
                    //ingAfegits.add(ingaux);
                    int pos = ingAfegits.indexOf(ingaux);
                    Integer qttaux;
                    String s = etQuantitat.getText().toString();
                    qttaux = new Integer(s);
                    quantitat = qttaux;
                    if (qttaux.intValue() <= 0) {   // comprovem que es valida
                        toast_quantitat_no_valida();
                        etQuantitat.setText("");
                        return;
                    }
                    quantitats.set(pos, qttaux);
                    mesura = mesuresAfegits.get(pos_lv_ingredients);
                    Log.d(LOG_TAG, "pos = " + pos + "\n mesura = " + mesura);
                    mesuresAfegits.set(pos, mesura);
                    ordenar_ingredient_afegit();
                    fusionar_info_ing_afegits();
                    lvIngredients.setAdapter(new ArrayAdapter<String>(this,
                            R.layout.support_simple_spinner_dropdown_item,
                            infoIngAfegits));
                    etQuantitat.setText("");
                    spIngredients.setSelection(0);
                }
            }
        }
        catch (Exception e) {
            String msg = "Problema afegint ingredient a la llista\n";
            msg += "Missatge: " + e.getMessage() + "\n";
            msg += "ingredient: " + ingredient;
            Log.d(LOG_TAG, msg);
        }
    }

    public void onClickAfegirSubstitut(View view) {
        try {
            if (etNom.getText().toString().isEmpty()) {
                Toast toast = Toast.makeText(this, "Introduir nom de la recepta",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (pos_lv_ingredients < 0) {
                String s = "Selecciona un ingredient";
                Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (etSubstitut.getText().toString().isEmpty()) {
                String s = "Escriu un ingredient substitut";
                Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            nom = etNom.getText().toString();
            String nomR = nom;
            String nomI = ingAfegits.get(pos_lv_ingredients);
            String subs = etSubstitut.getText().toString();
            SubstitutParticipa subsPart = new SubstitutParticipa(nomR, nomI, subs);
            if (check_substituts_repetits(subsPart))        //ja hi es?
                return;
            add_substitut_ordenat(subsPart);
            carregar_lv_substitut_participa();
            etSubstitut.setText("");
            lv_ing_o_lv_sub = 0;
            Log.d(LOG_TAG, "pos_lv_ingredients = " + pos_lv_ingredients);
            lvIngredients.setSelection(pos_lv_ingredients);
            int pos_aux = lvIngredients.getSelectedItemPosition();
            Log.d(LOG_TAG, "widget diu pos = " + pos_aux);
            lvIngredients.setSelection(pos_aux);
        }
        catch (Exception e) {
            String s = "Problema afegint substitut participa\n";
            s += "Missatge: " + e.getMessage();
            Log.d(LOG_TAG, s);
        }
    }

    /**
     * Accions privades de la classe
     */
    private void carregar_spinner() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        Cursor cursor = dbHelper.selectIngredients();
        cursor.moveToFirst();
        ingredients = new ArrayList<String>();
        mesures = new ArrayList<String>();
        ingredientsArrayList = new ArrayList<String>(); // AL<ing+mes> per spIng
        mesura = "";
        mesures.add(mesura);
        ingredients.add("Afegir ingredient ...");
        ingredients.add(cursor.getString(0));
        mesures.add(cursor.getString(1));
        String ingr = ingredients.get(0);
        ingredientsArrayList.add(ingr);
        int i = 1;
        while (cursor.moveToNext()) {
            ingredients.add(cursor.getString(0));
            mesures.add(cursor.getString(1));
            String ing = ingredients.get(i) + "  (" + mesures.get(i) + ")";
            ingredientsArrayList.add(ing);
            i++;
        }
        Log.d(LOG_TAG, "ingredientsArrayList: " + ingredientsArrayList);
        spIngredients.setAdapter(new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,
                ingredientsArrayList));
    }

    private void ordenar_ingredient_afegit () {
        int n = ingAfegits.size();
        String ingnou = ingAfegits.get(n - 1);
        Integer qttnou = quantitats.get(n - 1);
        String mesnou = mesuresAfegits.get(n - 1);
        boolean ordenat = false;
        for (int i = 0; i < n && !ordenat; i++) {
            Log.d(LOG_TAG, "i = " + i + ": " + ingAfegits);
            String ingi = ingAfegits.get(i);
            char cn = ingnou.toCharArray()[0];
            char ci = ingi.toCharArray()[0];
            if (cn < ci) {
                for (int j = n - 1; j > i; j--) {
                    String ingaux = ingAfegits.get(j - 1);
                    Integer qttaux = quantitats.get(j-1);
                    String mesaux = mesuresAfegits.get(j - 1);
                    ingAfegits.set(j, ingaux);
                    quantitats.set(j, qttaux);
                    mesuresAfegits.set(j, mesaux);
                    Log.d(LOG_TAG, "j = " + j + ": " + ingAfegits);
                }
                ingAfegits.set(i, ingnou);
                quantitats.set(i, qttnou);
                mesuresAfegits.set(i, mesnou);
                ordenat = true;
                Log.d(LOG_TAG, "final: " + ingAfegits);
            }
        }
    }

    //PRE: les llistes ingAfegits, mesures i quantitats estan ordenades
    private void fusionar_info_ing_afegits() {
        int n = quantitats.size();
        infoIngAfegits = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            String mes = mesuresAfegits.get(i);
            if (!mes.contentEquals("ml.") && !mes.contentEquals("grams")) {
                if (mes.contentEquals("cullaradeta") && quantitats.get(i).intValue() != 1)
                    mes = "cullaradetes";
                else if (mes.contentEquals("unitat") && quantitats.get(i).intValue() != 1)
                    mes += "s";
            }
            String s = ingAfegits.get(i) + ":  ";
            s += quantitats.get(i) + " " + mes;
            infoIngAfegits.add(s);
        }
    }

    private void eliminar_item_seleccionat_lv() {
        if (lv_ing_o_lv_sub == 0) {
            ingAfegits.remove(pos_lv_ingredients);
            quantitats.remove(pos_lv_ingredients);
            mesuresAfegits.remove(pos_lv_ingredients);
            fusionar_info_ing_afegits();
            lvIngredients.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.support_simple_spinner_dropdown_item,
                    infoIngAfegits));
            //lvIngredients.requestLayout();
            String s = "ingredients afegits: " + ingAfegits + "\n";
            s += "infoIngAfegits: " + infoIngAfegits + "\n";
            s += "pos item seleccionat: " + pos_lv_ingredients;
            Log.d(LOG_TAG, s);
            eliminar_substituts_ingredient(ingredient);
            carregar_lv_substitut_participa();
            pos_lv_ingredients = -1;
        }
        else if (lv_ing_o_lv_sub == 1) {    //eliminar substitut
            eliminar_substitut_ingredient(ingredient);
            carregar_lv_substitut_participa();
            lvIngredients.setSelection(pos_lv_ingredients);
        }
    }

    /*
     * Metode que mostra un Toast que diu que qtt > 0
     */
    private void toast_quantitat_no_valida() {
        Toast toast = Toast.makeText(this.getApplicationContext(), "Qtt enter > 0",
                Toast.LENGTH_SHORT);
        toast.show();
    }

    private void carregar_lv_substitut_participa() {
        //substituts que participen per aquest (recepta, ingredient)
        ArrayList<String> subsPartIngArrayList = new ArrayList<String>();
        int n = subsPartArrayList.size();
        if (lv_ing_o_lv_sub == 0) {
            for (int i = 0; i < n; i++) {
                SubstitutParticipa subspart = subsPartArrayList.get(i);
                Log.d(LOG_TAG, "ingredient = " + ingredient);
                Log.d(LOG_TAG, "nom_recept = " + subspart.getNom_recepta());
                Log.d(LOG_TAG, "recepta et = " + nom);
                Log.d(LOG_TAG, "nom_ing = " + subspart.getNom_ingredient());
                if (subspart.getNom_ingredient().contentEquals(ingredient) &&
                        subspart.getNom_recepta().contentEquals(nom)) {
                    String subs = subspart.getSubstitut();
                    Log.d(LOG_TAG, "Entro a carregar_lv_substitut_par");
                    subsPartIngArrayList.add(subs);
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, subsPartIngArrayList);
        lvSubstituts.setAdapter(adapter);
        //lvIngredients.setSelection(pos_lv_ingredients);
    }

    /*
     * retorna cert si el substitut participa ja hi es a subsPartArrayList
     */
    private boolean check_substituts_repetits(SubstitutParticipa subsPart) {
        boolean res = false;
        int i = 0;
        int n = subsPartArrayList.size();
        while (!res && i < n) {
            res = subsPartArrayList.get(i).equals(subsPart);
            i++;
        }
        return res;
    }

    private void add_substitut_ordenat(SubstitutParticipa subspar) {
        int n = subsPartArrayList.size();
        boolean ordenat = false;
        for (int i = 0; !ordenat && i < n; i++) {
            boolean b = !subsPartArrayList.get(i).getNom_recepta().contentEquals(subspar.getNom_recepta()) ||
                    !subsPartArrayList.get(i).getNom_ingredient().contentEquals(subspar.getNom_ingredient());
            /*if (b) {
                //no fem res
            }*/
            if (!b && subsPartArrayList.get(i).major_que(subspar)) {
                SubstitutParticipa spultim = subsPartArrayList.get(n - 1);
                subsPartArrayList.add(spultim);
                for (int j = n - 1; j > i; j--) {
                    SubstitutParticipa spaux = subsPartArrayList.get(j - 1);
                    subsPartArrayList.set(j, spaux);
                }
                subsPartArrayList.set(i, subspar);
                ordenat = true;
            }
        }
        if (!ordenat)
            subsPartArrayList.add(subspar);
    }

    //eliminem els ingredients substituts de l'ingredient parametre
    private void eliminar_substituts_ingredient(String ing) {
        int n = subsPartArrayList.size();
        for (int i = n - 1; i > 0; i--) {
            SubstitutParticipa spaux = subsPartArrayList.get(i);
            String spaux_rec = spaux.getNom_recepta();
            String spaux_ing = spaux.getNom_ingredient();
            if (spaux_rec.contentEquals(nom) && spaux_ing.contentEquals(ing)) {
                subsPartArrayList.remove(i);
            }
        }
        if (subsPartArrayList.size() == 1) {
            SubstitutParticipa spaux = subsPartArrayList.get(0);
            String spaux_rec = spaux.getNom_recepta();
            String spaux_ing = spaux.getNom_ingredient();
            if (spaux_rec.contentEquals(nom) && spaux_ing.contentEquals(ing)) {
                subsPartArrayList = new ArrayList<SubstitutParticipa>();
            }
        }
        pos_lv_substituts = -1;
    }

    //eliminar el substitut seleccionat de l'ingredient seleccionat
    private void eliminar_substitut_ingredient(String ing) {
        SubstitutParticipa spaux = subsPartArrayList.get(pos_lv_substituts);
        String spaux_rec = spaux.getNom_recepta();
        String spaux_ing = spaux.getNom_ingredient();
        if (spaux_rec.contentEquals(nom) && spaux_ing.contentEquals(ing)) {
            subsPartArrayList.remove(spaux);
        }
        pos_lv_substituts = -1;
    }

    private void get_dades_bd() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        Cursor cursor_recepta = dbHelper.select_recepta(nom);
        if ((cursor_recepta != null) && cursor_recepta.getCount() > 0) {
            cursor_recepta.moveToFirst();
            tipus = cursor_recepta.getString(cursor_recepta.getColumnIndex(
                    Contract.TRecepta.COLUMN_TIPUS));
            imatge = cursor_recepta.getString(cursor_recepta.getColumnIndex(
                    Contract.TRecepta.COLUMN_IMATGE));
            descripcio = cursor_recepta.getString(cursor_recepta.getColumnIndex(
                    Contract.TRecepta.COLUMN_DESCRIPCIO));
            etNom.setText(nom);
            if (tipus != null) etTipus.setText(tipus);
            else etTipus.setText("");

            if (descripcio != null) etDescripcio.setText(descripcio);
            else etDescripcio.setText("");

            Drawable d;
            if (imatge.contentEquals("-")) {
                d = getResources().getDrawable(R.drawable.food);
            }
            else {
                Bitmap imgRecepta = BitmapFactory.decodeFile(imatge);
                d = new BitmapDrawable(imgRecepta);
            }
            ivImatge.setBackground(d);

            Cursor cursor_participa = dbHelper.select_substituts_participa();
            if (cursor_participa != null && cursor_participa.getCount() > 0) {
                cursor_participa.moveToFirst();
                do {
                    String nom_r = cursor_participa.getString(cursor_participa.getColumnIndex(
                            Contract.TParticipa.COLUMN_RECEPTA));
                    if (nom_r.contentEquals(nom)) {
                        String ing = cursor_participa.getString(cursor_participa.getColumnIndex(
                                Contract.TSubstitutsParticipa.COLUMN_INGREDIENT));
                        ingAfegits.add(ing);
                    }

                }
                while (cursor_participa.moveToNext());
                lvIngredients.setAdapter(new ArrayAdapter<String>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        ingAfegits));
                int n = ingAfegits.size();
                for (int i = 0; i < n; i ++) {
                    String nom_r = nom;
                    String nom_i = ingAfegits.get(i);
                    Cursor cursor_subs = dbHelper.select_substituts_participa(nom_r, nom_i);
                    if (cursor_subs != null && cursor_subs.getCount() > 0) {
                        cursor_subs.moveToFirst();
                        do {
                            String subs = cursor_subs.getString(
                                    cursor_subs.getColumnIndex(
                                            Contract.TSubstitutsParticipa.COLUMN_SUBSTITUT));
                            SubstitutParticipa subspar = new SubstitutParticipa(nom_r, nom_i, subs);
                            subsPartArrayList.add(subspar);
                        }
                        while (cursor_subs.moveToNext());
                    }
                    carregar_lv_substitut_participa();
                }
            }
        }
        dbHelper.close();
    }
}
