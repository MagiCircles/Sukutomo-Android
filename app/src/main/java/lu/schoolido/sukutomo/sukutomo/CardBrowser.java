package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;


public class CardBrowser extends Activity implements View.OnClickListener {
    // We'll need these attributes to be static for now, in order to print Images from Card.java
    static ProgressDialog pDialog;
    static Bitmap bitmap;
    static ImageView img;
    private static String cardImageUrl;
    private final HttpClient Client;
    private String siteURL = "http://schoolido.lu/api/cards/";
    private static int currentCard = 1;
    private static LinkedList<Card> userCards;

    public CardBrowser() {
        Client = new DefaultHttpClient();
        userCards = new LinkedList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);

        img = (ImageView) findViewById(R.id.card_image);
        img.setOnClickListener(this);
        LoadCards li = new LoadCards();
        li.execute();
    }

    @Override
    public void onClick(View v) {
        CardBrowser.currentCard = (CardBrowser.currentCard + 1) % CardBrowser.userCards.size();
        Toast.makeText(CardBrowser.this, CardBrowser.userCards.get(CardBrowser.currentCard).getImageURL(), Toast.LENGTH_SHORT).show();
        CardBrowser.userCards.get(CardBrowser.currentCard).showImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void getCards(String data) throws JSONException {
        JSONObject obj = new JSONObject(data);
        JSONArray cardList = obj.getJSONArray("results");
        int n = cardList.length();
        for(int i=0; i < n; i++) {
            System.out.println(String.valueOf(cardList.getJSONObject(i)));
            userCards.add(new Card(cardList.getJSONObject(i)));
        }
        //JSONObject obj1 = cardList.getJSONObject(1);
        //cardImageUrl = obj1.getString("card_image");
        userCards.getFirst().showImage();
    }

    // I will refactor this class so that it can be used with any method you pass to it.
    // If I do that, we'll be able to simplify some things in the future.
    private class LoadCards extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CardBrowser.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            String data = "";
            HttpGet httpget = new HttpGet(siteURL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                data = Client.execute(httpget, responseHandler);
                getCards(data);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(cardImageUrl).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);
                pDialog.show();
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                //Toast.makeText(CardBrowser.this, "Not found", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
