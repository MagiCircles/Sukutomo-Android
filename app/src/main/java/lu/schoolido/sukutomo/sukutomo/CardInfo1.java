package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * CardInfo1 shows the main information for each card. It'll be possible to switch between this
 * Activity, CardInfo2 and CardBrowser activity.
 */
public class CardInfo1 extends ActionBarActivity {
    private Card card_info;
    private Button level_button1;
    private Button level_button2;
    private Button level_button3;
    private int[] min_stats;
    private int[] non_idolized_max_stats;
    private int[] idolized_max_stats;
    private TextView smile_stats_text;
    private TextView pure_stats_text;
    private TextView cool_stats_text;
    private ProgressBar smile_stats_bar;
    private ProgressBar pure_stats_bar;
    private ProgressBar cool_stats_bar;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info1);
        Intent intent = getIntent();
        card_info = (Card) intent.getParcelableExtra("card");
        mDetector = new GestureDetectorCompat(this, new GestureListener(this));
        // Filling text views
        TextView txt = (TextView) findViewById(R.id.card_id);
        txt.setText("#" + String.valueOf(card_info.getId()));

        txt = (TextView) findViewById(R.id.card_name);
        txt.setText(card_info.getName());
        txt = (TextView) findViewById(R.id.card_rarity);
        txt.setText(card_info.getRarity().toString());
        txt = (TextView) findViewById(R.id.card_attr);
        txt.setText(card_info.getAttribute().toString());
        txt = (TextView) findViewById(R.id.release_date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy", Locale.US);
        if(card_info.getRelease_date()!=null)
            txt.setText(sdf.format(card_info.getRelease_date()).toString());
        else
            txt.setText("Unknown");

        // Text views for stats
        min_stats = card_info.getMinimum_statistics();
        non_idolized_max_stats = card_info.getNon_idolized_maximum_statistics();
        idolized_max_stats = card_info.getIdolized_maximum_statistics();

        updateStats(min_stats);

        // Round Image
        ImageView im = (ImageView) findViewById(R.id.card_image);
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

    private class GestureListener extends GenericGestureListener {
        public GestureListener(Context context) {
            super(context);
        }

        @Override
        public boolean onSlideLeft() {
            finish();
            return true;
        }
    }
}
