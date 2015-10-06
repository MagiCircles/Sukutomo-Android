package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
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
    private LinkedList<ImageView> views;
    private int currentView = 0;
    private String siteURL = "http://schoolido.lu/api/cardids/";
    private static boolean showIdolized = false;
    private LinkedList<Integer> filteredCards;
    private int currentCardIndex = 0;
    // Next card, previous card, current card
    private Card[] currentCards = new Card[]{null, null, null};
    private GestureDetectorCompat mDetector;
    private final HttpClient Client;
    private static LruCache<String, Bitmap> imagesMemoryCache = new LruCache<String, Bitmap>(10);
    private static LruCache<Integer, Card> cardsMemoryCache = new LruCache<Integer, Card>(10);
    private static Drawable loadingImage;
    public static ImageView loadingView;
    public static Animation loadAnimation;
    static Context context;
    /**
     * Used for synchronization with AsyncTasks
     */
    private static boolean blocked = false;

    public CardBrowser() {
        Client = new DefaultHttpClient();
        filteredCards = new LinkedList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_browser);
        context = getApplicationContext();

        // Avoiding screen loading_rotation:
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

        // Saves the load image and its view, only visible when an image is loading.
        loadingImage = getResources().getDrawable(R.drawable.loading);
        loadingView = (ImageView) findViewById(R.id.loading_view);
        loadingView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_rotation));
        loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_rotation);


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

    /**
     * Blocks CardBrowser navigation (use during load).
     * @return Navigation already blocked.
     */
    public static boolean block() {
        if(blocked)
            return false;
        else
            return blocked = true;
    }

    /**
     * Releases CardBrowser navigation (use after load).
     * @return Navigation already released.
     */
    public static boolean release() {
        if(blocked)
            return !(blocked = false);
        else
            return blocked;
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
            if(!blocked) {
                showIdolized = !showIdolized;

                currentCards[2].showImage(showIdolized, views.get(currentView));
            }
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
        if(!blocked) {
            Intent info1 = new Intent(getApplicationContext(), CardInfo1.class);

            info1.putExtra("card", currentCards[2]);
            startActivity(info1);
            overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
        }
    }

    /**
     * Shows the slideUp animation and loads the previous card.
     */
    private void slideUp() {
        if(!blocked) {
            views.get(currentView).startAnimation(GenericGestureListener.slideExitUpAnimation);
            currentCardIndex = getPreviousCardIndex();
            changeViews();
            setCardBackground(currentCards[1], views.get(currentView));
            LoadCards li = new LoadCards(false);
            li.execute();
            views.get(currentView).startAnimation(GenericGestureListener.slideEnterDownAnimation);
        }
    }

    /**
     * Shows the slideDown animation and loads the next card.
     */
    private void slideDown() {
        if(!blocked) {
            views.get(currentView).startAnimation(GenericGestureListener.slideExitDownAnimation);
            currentCardIndex = getNextCardIndex();
            changeViews();
            setCardBackground(currentCards[0], views.get(currentView));

            LoadCards li = new LoadCards(false);
            li.execute();
            views.get(currentView).startAnimation(GenericGestureListener.slideEnterUpAnimation);
        }
    }

    /** Sets a background to the current view depending on current card's attribute.
     * @param card Card's to show
     * @param view Imageview to apply the background
     */
    public static void setCardBackground(Card card, ImageView view) {
        String assetPath = "";
        switch (card.getAttribute()) {
            case COOL:
                switch(card.getRarity()) {
                    case N:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_n_idol_cool.png";
                        else
                            assetPath = "cardback_n_nonidol_cool.png";
                        break;
                    case R:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_r_idol_cool.png";
                        else
                            assetPath = "cardback_r_nonidol_cool.png";
                        break;
                    case SR:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_sr_idol_cool.png";
                        else
                            assetPath = "cardback_sr_nonidol_cool.png";
                        break;
                    case UR:
                        assetPath = "cardback_ur_cool.png";
                        break;
                }
                break;
            case PURE:
                switch(card.getRarity()) {
                    case N:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_n_idol_pure.png";
                        else
                            assetPath = "cardback_n_nonidol_pure.png";
                        break;
                    case R:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_r_idol_pure.png";
                        else
                            assetPath = "cardback_r_nonidol_pure.png";
                        break;
                    case SR:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_sr_idol_pure.png";
                        else
                            assetPath = "cardback_sr_nonidol_pure.png";
                        break;
                    case UR:
                        assetPath = "cardback_ur_pure.png";
                        break;
                }
                break;
            case SMILE:
                switch(card.getRarity()) {
                    case N:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_n_idol_smile.png";
                        else
                            assetPath = "cardback_n_nonidol_smile.png";
                        break;
                    case R:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_r_idol_smile.png";
                        else
                            assetPath = "cardback_r_nonidol_smile.png";
                        break;
                    case SR:
                        if(showIdolized || card.is_promo())
                            assetPath = "cardback_sr_idol_smile.png";
                        else
                            assetPath = "cardback_sr_nonidol_smile.png";
                        break;
                    case UR:
                        assetPath = "cardback_ur_smile.png";
                        break;
                }
                break;
            default: assetPath = "cardback_ur_all.png";
        }

        Drawable drawable = BitmapAssetLoader.getAsset(assetPath, context.getResources());
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onTrimMemory(TRIM_MEMORY_RUNNING_MODERATE);
    }

    /**
     * @return Next card index
     */
    private int getNextCardIndex() {
        return (currentCardIndex + 1) % filteredCards.size();
    }

    /**
     * @return Previous card index
     */
    private int getPreviousCardIndex() {
        return currentCardIndex > 0 ? currentCardIndex - 1 : filteredCards.size() - 1;
    }

    /**
     * Change between the two card image views.
     */
    private void changeViews() {
        currentView = (currentView + 1) % 2;
        views.get(currentView).setImageDrawable(null);
    }

    /** Adds a downloaded bitmap to the Caché
     * @param key Image identificator. Usually its URL.
     * @param bitmap Downloaded bitmap.
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            imagesMemoryCache.put(key, bitmap);
        }
    }

    /** Adds a downloaded card to the Caché
     * @param key Card identificator.
     * @param card Downloaded card info.
     */
    public static void addCardToMemoryCache(int key, Card card) {
        if (getCardFromMemCache(key) == null) {
            cardsMemoryCache.put(key, card);
        }
    }

    /** Retrieves an image from the caché.
     * @param key Image identificator in the caché. Usually its URL.
     * @return Desired bitmap.
     */
    public static Bitmap getBitmapFromMemCache(String key) {
        return imagesMemoryCache.get(key);
    }

    /** Retrieves a card from the caché.
     * @param key Card identificator in the caché.
     * @return Desired card.
     */
    public static Card getCardFromMemCache(int key) {
        return cardsMemoryCache.get(key);
    }

    /**
     * Class in charge of the initial card id List and the card info download.
     */
    private class LoadCards extends AsyncTask<String, String, Void> {

        private final boolean initial;
        private AlertDialog.Builder errorAlert;
        private boolean success = true;

        /**
         * @param initial indicates if the object will be used for the initial download or a single card download.
         */
        protected LoadCards(boolean initial) {
            this.initial = initial;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Block the thread until Cardbrowser not blocked.
            CardBrowser.block();
            errorAlert = new AlertDialog.Builder(CardBrowser.this);
        }

        protected Void doInBackground(String... args) {
            String data = "";
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                if(initial) {
                    success = false;
                    int i = 0;
                    do {
                        try {
                            data = Client.execute(new HttpGet(siteURL), responseHandler);
                            success = true;
                        } catch (Exception ex) {
                            i++;
                        }

                    } while (!success && i < 3);
                    getCards(data);
                    currentCardIndex = filteredCards.size() - 1;
                }
                // First id: Next card
                // Second id: Previous card
                // Third id: Current card
                int[] ids = new int[]{filteredCards.get(getNextCardIndex()), filteredCards.get(getPreviousCardIndex()), filteredCards.get(currentCardIndex)};
                String cardString;
                Card card;
                for(int i = 0; i < 3; i++) {
                    card = getCardFromMemCache(ids[i]);
                    if (card == null) {
                        cardString = Client.execute(new HttpGet("http://schoolido.lu/api/cards/" + ids[i] + "/"), responseHandler);
                        card = new Card(new JSONObject(cardString));
                        CardBrowser.addCardToMemoryCache(ids[i], card);
                    }
                    currentCards[i] = card;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            CardBrowser.release();

            if (success) {
                // if download was successful, the card image should be shown.
                currentCards[2].showImage(showIdolized, views.get(currentView));
            } else {
                errorAlert.setTitle(R.string.ConnectionFailed);
                errorAlert.setMessage(R.string.ConnectionRetry);
                errorAlert.show();
            }
        }
    }
}
