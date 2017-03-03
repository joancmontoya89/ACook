package montoya.girona.joan.idi.fib.acook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by joangmontoya on 7/1/16.
 */
public abstract class CustomAdapter extends BaseAdapter {

    /**
     * Atributs privats de la classe
     */
    private final String LOG_TAG = CustomAdapter.class.getSimpleName();
    private ArrayList<?> entrades;
    private Context context;


    /**
     * Metodes publics de la classe
     */

    public CustomAdapter(Context context, ArrayList<?> dades_entrada) {
        super();
        entrades = dades_entrada;
        this.context = context;
    }

    @Override
    public int getCount() {
        int n = 0;
        if (entrades != null)
            n = entrades.size();
        return n;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.recepta_nom_imatge, null);
        }
        onEntrada(entrades.get(position), view);
        return view;
    }

    /** Retorna totes les entrades amb les vistes a les que s'ha d'associar
     * @param entrada
     * @param view
     */
    public abstract void onEntrada (Object entrada, View view);


}
