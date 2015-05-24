package lu.schoolido.sukutomo.sukutomo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arukantara on 23/05/15.
 */
public class Card {
    // General
    private int id = 0;
    private String name = "";
    private String japanese_name = "";
    private String collection = "";
    private String japanese_collection = "";
    private Attribute attribute = Attribute.SMILE;
    private boolean japan_only = false;
    private String card_image = "";
    private String round_card_image = "";
    private Rarity rarity = Rarity.N;

    // IdolCard
    private String event = "";
    private int hp = 0;
    private List<Integer> minimum_statistics = new ArrayList<Integer>(3);
    private List<Integer> non_idolized_maximum_statistics = new ArrayList<Integer>(3);
    private List<Integer> idolized_maximum_statistics = new ArrayList<Integer>(3);
    private String card_idolized_image = "";
    private String video_story = null;
    private String japanese_video_story = null;

    // Support and RareCard
    private String skill = "None";
    private String japanese_skill = "無し";
    //private String skill_details = "None";
    //private String japanese_skill_details = "無し";

    // RareCard
    private boolean is_promo = false;
    private String promo_item = null;
    private boolean is_special = false;
    private String center_skill = "None";
    private String japanese_center_skill = "無し";
    //private Bitmap bitmap;
    //private ImageView img;

    public Card(JSONObject object) throws JSONException {
        card_image = object.getString("card_image");
        card_idolized_image = object.getString("card_idolized_image");
    }

    public String getImageURL(boolean idolized) {
        if(idolized)
            return card_image;
        else
            return card_idolized_image;
    }

    public void showImage(boolean idolized, Bitmap bitmap) {
        if(idolized)
            new LoadImage(bitmap).execute(card_image);
        else
            new LoadImage(bitmap).execute(card_idolized_image);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;
        protected LoadImage(Bitmap bitmap) {
            bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                CardBrowser.img.setImageBitmap(image);
                //CardBrowser.pDialog.show();
                //CardBrowser.pDialog.dismiss();

            }
        }
    }
}
