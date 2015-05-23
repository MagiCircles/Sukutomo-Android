package lu.schoolido.sukutomo.sukutomo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private String jpName = "";
    private String collection = "";
    private String jpCollection = "";
    private Attribute attribute = Attribute.SMILE;
    private boolean japanOnly = false;
    private String image = "";
    private String roundImage = "";
    private Rarity rarity = Rarity.N;

    // IdolCard
    private String event = "";
    private int maxHP = 0;
    private List<Integer> minStats = new ArrayList<Integer>(3);
    private List<Integer> maxStats = new ArrayList<Integer>(3);
    private List<Integer> maxIdolStats = new ArrayList<Integer>(3);
    private String idolImage = "";
    private String videoStory = null;

    // Support and RareCard
    private String skill = "None";
    private String jpSkill = "無し";

    // RareCard
    private boolean isPromo = false;
    private String promoItem = null;
    private boolean isSpecial = false;
    private String centerSkill = "None";
    private String jpCenterSkill = "無し";
    //private Bitmap bitmap;
    //private ImageView img;

    public Card(JSONObject object) throws JSONException {
        image = object.getString("card_image");
    }

    public String getImageURL() {
        return image;
    }

    public void showImage() {
        new LoadImage().execute(image);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                CardBrowser.bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return CardBrowser.bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                CardBrowser.img.setImageBitmap(image);
                //pDialog.show();
                //pDialog.dismiss();

            }
        }
    }
}
