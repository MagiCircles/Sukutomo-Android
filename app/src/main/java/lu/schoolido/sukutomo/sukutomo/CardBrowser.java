package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;


public class CardBrowser extends Activity {
    private ProgressDialog pDialog;
    private LinkedList<ImageView> views;
    private int currentView = 0;
    //private String cardImageUrl;
    private String siteURL = "http://schoolido.lu/api/cardids/";
    private boolean showIdolized = false;
    private LinkedList<Integer> filteredCards;
    private int currentCardIndex = 0;
    private Card currentCard;
    private GestureDetectorCompat mDetector;
    private final HttpClient Client;
    private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(25);
    private static Drawable loadingImage;
    private final int CARDS_HEIGHT = 720;
    private final int CARDS_WIDTH = 512;
    private Matrix m;
    //private LoadCards li;

    public CardBrowser() {
        Client = new DefaultHttpClient();
        filteredCards = new LinkedList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);

        // Avoiding screen rotation:
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // If a search of cards by idols or other criteria has been requested.
        Intent intent = getIntent();
        if(intent.hasExtra("url"))
            siteURL = intent.getStringExtra("url");

        // Creating the views list. There are two views, allowing changing from one card to the next
        // with a fluent animation.
        views = new LinkedList<>();
        views.add((ImageView) findViewById(R.id.card_image));
        views.add((ImageView) findViewById(R.id.card_image2));

        // Getting screen height
        Display display = getWindowManager().getDefaultDisplay();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        int height;

        Point size = new Point();
        display.getSize(size);
        height = size.y;

        m = views.getFirst().getImageMatrix();
        m.reset();
        m.setScale((height*0.96f)/CARDS_HEIGHT, (height*0.96f)/CARDS_HEIGHT);
        //RectF drawableRect = new RectF(0, 0, CARDS_WIDTH, CARDS_HEIGHT);
        //RectF viewRect = new RectF(0, 0, views.getFirst().getWidth(), views.getFirst().getHeight());
        //m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.START);
        views.getFirst().setImageMatrix(m);


        // Shows the load image until the card image has been downloaded.
        loadingImage = getResources().getDrawable(R.drawable.loading);

        // Preparing gestures:
        mDetector = new GestureDetectorCompat(this, new GestureListener(this));

        // Preparing menu button:
        ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(getApplicationContext(), MenuActivity.class);

                startActivity(menu);
                //overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
            }
        });

        // Up button and slide down.
        ImageView upButton = (ImageView) findViewById(R.id.arrow_up);
        upButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_down));
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown();
            }
        });


        // Down button and slide up.
        ImageView downButton = (ImageView) findViewById(R.id.arrow_down);
        downButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_up));
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp();
            }
        });

        // Right button and slide left.
        ImageView rightButton = (ImageView) findViewById(R.id.arrow_right);
        rightButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_right));
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideLeft();
            }
        });

        // Loads the initial card info.
        LoadCards li = new LoadCards(true);
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

    /** Get the card ids list from the JSONObject returned by the URL used.
     * @param data
     * @throws JSONException
     */
    protected void getCards(String data) throws JSONException {
        JSONArray cardList = new JSONArray(data);

        Log.d("getCards", "JSONObject: " + cardList.toString());
        int n = cardList.length();
        for(int i=0; i < n; i++) {
            filteredCards.add(cardList.getInt(i));
        }
    }


    /**
     * Class used to manage screen gestures.
     */
    private final class GestureListener extends GenericGestureListener {
        private final String TAG = GestureListener.class.getSimpleName();

        private static final int SLIDE_THRESHOLD = 100;

        public GestureListener(Context context) {
            super(context);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            showIdolized = !showIdolized;

            currentCard.showImage(showIdolized, views.get(currentView));
            return true;
        }

        @Override
        public boolean onSlideRight() {
            return false;
        }

        @Override
        public boolean onSlideLeft() {
            slideLeft();
            return true;
        }

        @Override
        public boolean onSlideUp() {
            slideUp();
            return true;
        }

        @Override
        public boolean onSlideDown() {
            slideDown();
            return true;
        }

    }

    private void slideLeft() {
        Intent info1 = new Intent(getApplicationContext(), CardInfo1.class);

        info1.putExtra("card", currentCard);
        startActivity(info1);
        overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
    }

    /**
     * Shows the slideUp animation and loads the previous card.
     */
    private void slideUp() {
        views.get(currentView).startAnimation(GenericGestureListener.slideExitUpAnimation);
        if (currentCardIndex > 0)
            currentCardIndex = currentCardIndex - 1;
        else
            currentCardIndex = filteredCards.size() - 1;
        changeViews();
        LoadCards li = new LoadCards(false);
        li.execute();
        views.get(currentView).startAnimation(GenericGestureListener.slideEnterDownAnimation);
    }

    /**
     * Shows the slideDown animation and loads the next card.
     */
    private void slideDown() {
        views.get(currentView).startAnimation(GenericGestureListener.slideExitDownAnimation);
        currentCardIndex = (currentCardIndex + 1) % filteredCards.size();
        changeViews();

        LoadCards li = new LoadCards(false);
        li.execute();
        views.get(currentView).startAnimation(GenericGestureListener.slideEnterUpAnimation);
    }

    /**
     * Change between the two card image views.
     */
    private void changeViews() {
        currentView = (currentView + 1) % 2;
        views.get(currentView).setImageDrawable(loadingImage);
    }

    /** Adds a downloaded bitmap to the Caché
     * @param key Image identificator. Usually its URL.
     * @param bitmap Downloaded bitmap.
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /** Retrieves an image from the caché.
     * @param key Image identificator in the caché. Usually its URL.
     * @return Desired bitmap.
     */
    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Class in charge of the initial card id List and the card info download.
     */
    private class LoadCards extends AsyncTask<String, String, Void> {

        private final boolean initial;

        /**
         * @param initial indicates if the object will be used for the initial download or a single card download.
         */
        protected LoadCards(boolean initial) {
            this.initial = initial;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CardBrowser.this);
            pDialog.setMessage("Loading cards ...");
            //pDialog.show();
        }

        protected Void doInBackground(String... args) {
            String data = "";
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                if(initial) {
                    data = Client.execute(new HttpGet(siteURL), responseHandler);
                    getCards(data);
                }
                String card = Client.execute(new HttpGet("http://schoolido.lu/api/cards/" + filteredCards.get(currentCardIndex) + "/"), responseHandler);
                currentCard = new Card(new JSONObject(card));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            pDialog.show();
            pDialog.dismiss();
            currentCard.showImage(showIdolized, views.get(currentView));
        }
    }
}
