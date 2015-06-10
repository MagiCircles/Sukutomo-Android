package lu.schoolido.sukutomo.sukutomo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class CardInfo1 extends ActionBarActivity {
    private Card card_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info1);
        Intent intent = getIntent();
        card_info = (Card) intent.getParcelableExtra("card");
        // Filling text views
        TextView txt = (TextView) findViewById(R.id.card_id);

        txt.setText(String.valueOf(card_info.getId()));
        txt = (TextView) findViewById(R.id.card_name);
        txt.setText(card_info.getName());
        txt = (TextView) findViewById(R.id.card_rarity);
        txt.setText(card_info.getRarity().toString());
        txt = (TextView) findViewById(R.id.card_attr);
        txt.setText(card_info.getAttribute().toString());
        txt = (TextView) findViewById(R.id.release_date);
        //txt.setText(card_info.getRelease_date().toString());

        // Text views for stats
        int[] min_stats = card_info.getMinimum_statistics();
        int[] non_idolized_max_stats = card_info.getNon_idolized_maximum_statistics();
        int[] idolized_max_stats = card_info.getIdolized_maximum_statistics();

        txt = (TextView) findViewById(R.id.smile_val1);
        txt.setText(String.valueOf(min_stats[0]));
        txt = (TextView) findViewById(R.id.pure_val1);
        txt.setText(String.valueOf(min_stats[1]));
        txt = (TextView) findViewById(R.id.cool_val1);
        txt.setText(String.valueOf(min_stats[2]));

        txt = (TextView) findViewById(R.id.smile_val2);
        txt.setText(String.valueOf(non_idolized_max_stats[0]));
        txt = (TextView) findViewById(R.id.pure_val2);
        txt.setText(String.valueOf(non_idolized_max_stats[1]));
        txt = (TextView) findViewById(R.id.cool_val2);
        txt.setText(String.valueOf(non_idolized_max_stats[2]));

        txt = (TextView) findViewById(R.id.smile_val3);
        txt.setText(String.valueOf(idolized_max_stats[0]));
        txt = (TextView) findViewById(R.id.pure_val3);
        txt.setText(String.valueOf(idolized_max_stats[1]));
        txt = (TextView) findViewById(R.id.cool_val3);
        txt.setText(String.valueOf(idolized_max_stats[2]));

        // ProgressBar for stats
        ProgressBar bar = (ProgressBar) findViewById(R.id.smile_bar1);
        bar.setProgress(min_stats[0]);
        bar = (ProgressBar) findViewById(R.id.pure_bar1);
        bar.setProgress(min_stats[1]);
        bar = (ProgressBar) findViewById(R.id.cool_bar1);
        bar.setProgress(min_stats[2]);

        bar = (ProgressBar) findViewById(R.id.smile_bar2);
        bar.setProgress(non_idolized_max_stats[0]);
        bar = (ProgressBar) findViewById(R.id.pure_bar2);
        bar.setProgress(non_idolized_max_stats[1]);
        bar = (ProgressBar) findViewById(R.id.cool_bar2);
        bar.setProgress(non_idolized_max_stats[2]);

        bar = (ProgressBar) findViewById(R.id.smile_bar3);
        bar.setProgress(idolized_max_stats[0]);
        bar = (ProgressBar) findViewById(R.id.pure_bar3);
        bar.setProgress(idolized_max_stats[1]);
        bar = (ProgressBar) findViewById(R.id.cool_bar3);
        bar.setProgress(idolized_max_stats[2]);

        // Round Image
        ImageView im = (ImageView) findViewById(R.id.card_image);
        card_info.showRoundImage(null, im);
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
}
