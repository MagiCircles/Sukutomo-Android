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


public class CardBrowser extends Activity {
    private ProgressDialog pDialog;
    private Bitmap bitmap;
    private static ImageView img;
    private static String cardImageUrl;
    private final HttpClient Client;
    private String siteURL = "http://schoolido.lu/api/cards/";
    private static int currentCard = 1;
    private static int userCards;
    // Crear clase Card que modele las cartas, y almacenarlas en userCards
    public CardBrowser() {
        Client = new DefaultHttpClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);

        img = (ImageView) findViewById(R.id.card_image);
        LoadCards li = new LoadCards();
        li.execute();
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

    protected void evalu(String data) throws JSONException {
        Log.d("Debug", "Dentro del evalu");
        JSONObject obj = new JSONObject(data);
        JSONArray cardList = obj.getJSONArray("results");
        int n = cardList.length();
        for(int i=0; i < n && i < 9; i++) {
            System.out.println(String.valueOf(cardList.getJSONObject(i)));
        }
        JSONObject obj1 = cardList.getJSONObject(1);
        cardImageUrl = obj1.getString("card_image");
        // Los Toast parecen entorpecer la impresión de imágenes en un mismo hilo.
        //Toast.makeText(MainActivity.this, cardImageUrl, Toast.LENGTH_SHORT).show();
    }
    // http://www.learn2crack.com/2014/06/android-load-image-from-internet.html
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
                evalu(data);
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
                Toast.makeText(CardBrowser.this, "Not found", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
