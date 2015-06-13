package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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


public class CardBrowser extends Activity {
    static ProgressDialog pDialog;
    static Bitmap bitmap;
    static ImageView img;
    static String cardImageUrl;
    private String siteURL = "http://schoolido.lu/api/cards/?page=30";
    private static int currentCard = 0;
    private static boolean showIdolized = false;
    private static LinkedList<Card> userCards;
    private GestureDetectorCompat mDetector;
    private final HttpClient Client;

    public CardBrowser() {
        Client = new DefaultHttpClient();
        userCards = new LinkedList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);

        img = (ImageView) findViewById(R.id.card_image);
        mDetector = new GestureDetectorCompat(this, new GestureListener(this));

        LoadCards li = new LoadCards();
        li.execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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

    protected static void getCards(String data) throws JSONException {
        JSONObject obj = new JSONObject(data);
        JSONArray cardList = obj.getJSONArray("results");
        int n = cardList.length();
        Log.d("getCards", n + " cartas");
        for(int i=0; i < n; i++) {
            System.out.println(String.valueOf(cardList.getJSONObject(i)));
            userCards.add(new Card(cardList.getJSONObject(i)));
        }
        //JSONObject obj1 = cardList.getJSONObject(1);
        //cardImageUrl = obj1.getString("card_image");
        showIdolized = false;
        userCards.getFirst().showImage(showIdolized, bitmap, img);
    }



    private final class GestureListener extends GenericGestureListener {
        private final String TAG = GestureListener.class.getSimpleName();

        private static final int SLIDE_THRESHOLD = 100;

        public GestureListener(Context context) {
            super(context);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            showIdolized = !showIdolized;
            //Toast.makeText(CardBrowser.this, userCards.get(currentCard).getImageURL(showIdolized), Toast.LENGTH_SHORT).show();
            userCards.get(currentCard).showImage(showIdolized, bitmap, img);
            return true;
        }

        @Override
        public boolean onSlideLeft() {
            return false;
        }

        @Override
        public boolean onSlideRight() {
            Intent info1 = new Intent(getApplicationContext(), CardInfo1.class);

            info1.putExtra("card", userCards.get(currentCard));
            startActivity(info1);
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
            return true;
        }

        @Override
        public boolean onSlideUp() {
            img.startAnimation(super.slideUpAnimation);
            if (currentCard > 1)
                currentCard = currentCard - 1;
            else
                currentCard = userCards.size() - 1;

            //Toast.makeText(CardBrowser.this, userCards.get(currentCard).getImageURL(showIdolized), Toast.LENGTH_SHORT).show();
            userCards.get(currentCard).showImage(showIdolized, bitmap, img);

            return true;
        }

        @Override
        public boolean onSlideDown() {
            img.startAnimation(super.slideDownAnimation);
            currentCard = (currentCard + 1) % userCards.size();

            //Toast.makeText(CardBrowser.this, userCards.get(currentCard).getImageURL(showIdolized), Toast.LENGTH_SHORT).show();
            userCards.get(currentCard).showImage(showIdolized, bitmap, img);
            return true;
        }

    }

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
