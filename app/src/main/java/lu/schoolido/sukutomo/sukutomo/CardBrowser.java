package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


public class CardBrowser extends Activity /*implements View.OnClickListener*/ {
    // We'll need these attributes to be static for now, in order to print Images from Card.java
    private ProgressDialog pDialog;
    static Bitmap bitmap;
    static ImageView img;
    private static String cardImageUrl;
    private final HttpClient Client;
    private String siteURL = "http://schoolido.lu/api/cards/";
    private static int currentCard = 0;
    private static boolean showIdolized = false;
    private static LinkedList<Card> userCards;
    private static Animation slideUpAnimation;
    private static Animation slideDownAnimation;
    private static Animation slideRightAnimation;
    private GestureDetectorCompat mDetector;

    public CardBrowser() {
        Client = new DefaultHttpClient();
        userCards = new LinkedList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);

        img = (ImageView) findViewById(R.id.card_image);
        mDetector = new GestureDetectorCompat(this, new GestureListener());

        slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_exit_up);
        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_exit_down);
        slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_exit_right);
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
        showIdolized = false;
        userCards.getFirst().showImage(showIdolized, bitmap, img);
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


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final String TAG = GestureListener.class.getSimpleName();

        private static final int SLIDE_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                float deltaY = e2.getY() - e1.getY();
                float deltaX = e2.getX() - e1.getX();

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > SLIDE_THRESHOLD) {
                        if (deltaX > 0) {
                            return onSlideRight();
                        } else {
                            return onSlideLeft();
                        }
                    }/* else {
                        return onTouchEvent();
                    }*/
                } else {
                    if (Math.abs(deltaY) > SLIDE_THRESHOLD) {
                        if (deltaY > 0) {
                            return onSlideDown();
                        } else {
                            return onSlideUp();
                        }
                    } /*else {
                        return onTouchEvent();
                    }*/
                }
            } catch (Exception exception) {
                Log.e(TAG, exception.getMessage());
            }

            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            showIdolized = !showIdolized;
            Toast.makeText(CardBrowser.this, userCards.get(currentCard).getImageURL(showIdolized), Toast.LENGTH_SHORT).show();
            userCards.get(currentCard).showImage(showIdolized, bitmap, img);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onSlideLeft() {
            return false;
        }

        public boolean onSlideRight() {
            Intent info1 = new Intent(getApplicationContext(), CardInfo1.class);
            img.startAnimation(slideRightAnimation);
            startActivity(info1);
            info1.putExtra("card", userCards.get(currentCard));
            return true;
        }

        public boolean onSlideUp() throws InterruptedException {
            img.startAnimation(slideUpAnimation);
            if (currentCard > 1)
                currentCard = currentCard - 1;
            else
                currentCard = userCards.size() - 1;

            Toast.makeText(CardBrowser.this, userCards.get(currentCard).getImageURL(showIdolized), Toast.LENGTH_SHORT).show();
            userCards.get(currentCard).showImage(showIdolized, bitmap, img);
            /*try {
                wait(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            img.startAnimation(zoomInAnimation);*/
            return true;
        }

        public boolean onSlideDown() {
            img.startAnimation(slideDownAnimation);
            currentCard = (currentCard + 1) % userCards.size();
            Toast.makeText(CardBrowser.this, userCards.get(currentCard).getImageURL(showIdolized), Toast.LENGTH_SHORT).show();
            userCards.get(currentCard).showImage(showIdolized, bitmap, img);
            /*try {
                wait(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            img.startAnimation(slideEnterDownAnimation);*/
            return true;
        }

    }
}
