package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class prepares the lists elements for the IdolsSearchActivity.
 */
public class IdolsAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    ArrayList<String> data = null;

    public IdolsAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    static class IdolHolder {
        ImageView icon;
        TextView text;
    }

    /** This method adds a customized color to the list elements if it has the name of one of the main idols.
     * @param position List item position
     * @param convertView View to be converted. Not used currently
     * @param parent Parent of the list element
     * @return final element to be printed instead of the original list item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        int background, drawableItem;
        IdolHolder holder = new IdolHolder();

        if(data.get(position).equalsIgnoreCase("Ayase Eli")) {
            background = R.drawable.shape_eli;
            drawableItem = R.drawable.chibi_eli1;
        } else if(data.get(position).equalsIgnoreCase("Hoshizora Rin")) {
            background = R.drawable.shape_rin;
            drawableItem = R.drawable.chibi_rin1;
        } else if(data.get(position).equalsIgnoreCase("Koizumi Hanayo")) {
            background = R.drawable.shape_hanayo;
            drawableItem = R.drawable.chibi_hanayo1;
        } else if(data.get(position).equalsIgnoreCase("Kousaka Honoka")) {
            background = R.drawable.shape_honoka;
            drawableItem = R.drawable.chibi_honoka1;
        } else if(data.get(position).equalsIgnoreCase("Minami Kotori")) {
            background = R.drawable.shape_kotori;
            drawableItem = R.drawable.chibi_kotori1;
        } else if(data.get(position).equalsIgnoreCase("Nishikino Maki")) {
            background = R.drawable.shape_maki;
            drawableItem = R.drawable.chibi_maki1;
        } else if(data.get(position).equalsIgnoreCase("Sonoda Umi")) {
            background = R.drawable.shape_umi;
            drawableItem = R.drawable.chibi_umi1;
        } else if(data.get(position).equalsIgnoreCase("Toujou Nozomi")) {
            background = R.drawable.shape_nozomi;
            drawableItem = R.drawable.chibi_nozomi1;
        } else if(data.get(position).equalsIgnoreCase("Yazawa Nico")) {
            background = R.drawable.shape_nico;
            drawableItem = R.drawable.chibi_nico1;
        } else {
            background = R.drawable.list_item_shape;
            drawableItem = R.drawable.dummy;
        }


        if(drawableItem != R.drawable.dummy) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.main_idol_list_item, parent, false);
        } else {
            row = LayoutInflater.from(getContext()).inflate(R.layout.idol_list_item, parent, false);
        }
        row.setBackgroundResource(background);
        holder.text = (TextView) row.findViewById(R.id.item_text);
        holder.icon = (ImageView) row.findViewById(R.id.item_image);

        String item = getItem(position);
        holder.text.setText(item.toString());
        //holder.icon.setImageResource(drawableItem);
        holder.icon.setImageBitmap(BitmapLoader.decodeSampledBitmapFromResource(context.getResources(),
                drawableItem, 64, 64));

        return row;
    }

}
