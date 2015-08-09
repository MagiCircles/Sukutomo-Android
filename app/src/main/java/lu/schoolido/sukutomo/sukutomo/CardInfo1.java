package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;



/**
 * CardInfo1 shows the main information for each card. It'll be possible to switch between this
 * Activity, CardInfo2 and CardBrowser activity.
 */
public class CardInfo1 extends Activity {
    private Card card_info;
    private Button level_button1;
    private Button level_button2;
    private Button level_button3;
    private int[] min_stats;
    private int[] non_idolized_max_stats;
    private int[] idolized_max_stats;
    private int hp;
    private TextView smile_stats_text;
    private TextView pure_stats_text;
    private TextView cool_stats_text;
    private ProgressBar smile_stats_bar;
    private ProgressBar pure_stats_bar;
    private ProgressBar cool_stats_bar;
    private GestureDetectorCompat mDetector;
    private ImageView im;
    private static String baseURL = "http://schoolido.lu/api/cardids/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info1);
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView txt;
        RelativeLayout layout;
        Intent intent = getIntent();
        card_info = (Card) intent.getParcelableExtra("card");
        mDetector = new GestureDetectorCompat(this, new GestureListener(this));

        // Showing different colors based on card attribute
        switch (card_info.getAttribute()) {
            case SMILE:
                layout = (RelativeLayout) findViewById(R.id.card_info1);
                layout.setBackgroundResource(R.drawable.smile_background);
                txt = (TextView) findViewById(R.id.card_id);
                txt.setBackgroundResource(R.drawable.title_back_smile);
                txt = (TextView) findViewById(R.id.release_date_text);
                txt.setBackgroundResource(R.drawable.title_back_smile);
                txt = (TextView) findViewById(R.id.stats_text);
                txt.setBackgroundResource(R.drawable.title_back_smile);
                break;
            case PURE:
                layout = (RelativeLayout) findViewById(R.id.card_info1);
                layout.setBackgroundResource(R.drawable.pure_background);
                txt = (TextView) findViewById(R.id.card_id);
                txt.setBackgroundResource(R.drawable.title_back_pure);
                txt = (TextView) findViewById(R.id.release_date_text);
                txt.setBackgroundResource(R.drawable.title_back_pure);
                txt = (TextView) findViewById(R.id.stats_text);
                txt.setBackgroundResource(R.drawable.title_back_pure);
                break;
            case COOL:
                layout = (RelativeLayout) findViewById(R.id.card_info1);
                layout.setBackgroundResource(R.drawable.cool_background);
                txt = (TextView) findViewById(R.id.card_id);
                txt.setBackgroundResource(R.drawable.title_back_cool);
                txt = (TextView) findViewById(R.id.release_date_text);
                txt.setBackgroundResource(R.drawable.title_back_cool);
                txt = (TextView) findViewById(R.id.stats_text);
                txt.setBackgroundResource(R.drawable.title_back_cool);
                break;
        }
        // Filling text views
        txt = (TextView) findViewById(R.id.card_id);
        txt.setText(getString(R.string.Card) + " #" + String.valueOf(card_info.getId()));

        // Name field
        txt = (TextView) findViewById(R.id.card_name);
        txt.setText(card_info.getName());
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch("name", card_info.getName());
            }
        });

        // Rarity field
        txt = (TextView) findViewById(R.id.card_rarity);
        txt.setText(card_info.getRarity().toString());
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch("rarity", card_info.getRarity().toString());
            }
        });

        // Attribute
        txt = (TextView) findViewById(R.id.card_attr);
        txt.setText(card_info.getAttribute().toString());
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attr = card_info.getAttribute().toString();
                char first = Character.toUpperCase(attr.charAt(0));
                startSearch("attribute", first + attr.substring(1).toLowerCase());
            }
        });


        // Release Date
        txt = (TextView) findViewById(R.id.release_date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy", Locale.US);
        if(card_info.getRelease_date()!=null)
            txt.setText(sdf.format(card_info.getRelease_date()).toString());
        else
            txt.setText(R.string.unknown);

        // Text views for stats
        min_stats = card_info.getMinimum_statistics();
        non_idolized_max_stats = card_info.getNon_idolized_maximum_statistics();
        idolized_max_stats = card_info.getIdolized_maximum_statistics();
        hp = card_info.getHp();
        txt = (TextView) findViewById(R.id.hp1);
        txt.setText(String.valueOf(hp));

        updateStats(min_stats);

        // Round Image
        im = (ImageView) findViewById(R.id.roundImage);
        card_info.showRoundImage(null, im);

        level_button1 = (Button) findViewById(R.id.level_button1);
        level_button1.setText(R.string.level_1);
        level_button2 = (Button) findViewById(R.id.level_button2);
        level_button3 = (Button) findViewById(R.id.level_button3);

        switch(card_info.getRarity()) {
            case N: level_button2.setText(R.string.level_30);
                    level_button3.setText(R.string.level_40);
                    break;
            case R: level_button2.setText(R.string.level_40);
                    level_button3.setText(R.string.level_60);
                    break;
            case SR: level_button2.setText(R.string.level_60);
                    level_button3.setText(R.string.level_80);
                    break;
            case UR: level_button2.setText(R.string.level_80);
                    level_button3.setText(R.string.level_100);
                    break;
        }

        level_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateStats(min_stats);
            }
        });
        level_button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateStats(non_idolized_max_stats);
            }
        });
        level_button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateStats(idolized_max_stats);
            }
        });

        if(card_info.is_promo()) {
            level_button2.setVisibility(View.INVISIBLE);
        }

    }

    /** Creates a new CardBrowser Activity with the requested filter.
     * This may create some activities and consume memory, but gives to the users
     * the option to turn back over their steps.
     * @param field Field to filter.
     * @param value Value to apply to the filter
     */
    private void startSearch(String field, String value) {
        Intent search = new Intent(getApplicationContext(), CardBrowser.class);

        search.putExtra("url", baseURL + "?" + field + "=" + value.replace(" ", "%20"));
        startActivity(search);
    }

    /** Updates the stats bars and text with the stats received.
     * @param stats int[3], stats to apply (minimum, maximum, or idolized maximum)
     */
    private void updateStats(int[] stats) {
        smile_stats_text = (TextView) findViewById(R.id.smile_val1);
        smile_stats_text.setText(String.valueOf(stats[0]));
        pure_stats_text = (TextView) findViewById(R.id.pure_val1);
        pure_stats_text.setText(String.valueOf(stats[1]));
        cool_stats_text = (TextView) findViewById(R.id.cool_val1);
        cool_stats_text.setText(String.valueOf(stats[2]));

        // ProgressBar for stats
        smile_stats_bar = (ProgressBar) findViewById(R.id.smile_bar1);
        smile_stats_bar.setProgress(stats[0]);
        pure_stats_bar = (ProgressBar) findViewById(R.id.pure_bar1);
        pure_stats_bar.setProgress(stats[1]);
        cool_stats_bar = (ProgressBar) findViewById(R.id.cool_bar1);
        cool_stats_bar.setProgress(stats[2]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_info1, menu);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class GestureListener extends GenericGestureListener {
        public GestureListener(Context context) {
            super(context);
        }

        @Override
        public boolean onSlideRight() {
            finish();
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
            return true;
        }

        @Override
        public boolean onSlideLeft() {
            Intent info2 = new Intent(getApplicationContext(), CardInfo2.class);

            info2.putExtra("card", card_info);
            startActivity(info2);
            overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
            return true;
        }
    }
}
