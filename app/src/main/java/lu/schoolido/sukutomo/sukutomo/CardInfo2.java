package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;


public class CardInfo2 extends Activity {

    private GestureDetectorCompat mDetector;
    private Card card_info;
    private ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info2);
        Intent intent = getIntent();
        card_info = (Card) intent.getParcelableExtra("card");
        mDetector = new GestureDetectorCompat(this, new GestureListener(this));
        // Filling text views
        TextView txt = (TextView) findViewById(R.id.skill_name);
        if(card_info.getSkill()==null || card_info.getSkill().equalsIgnoreCase("none")  || card_info.getSkill_details().equalsIgnoreCase("null"))
            txt.setText(getString(R.string.none));
        else
            txt.setText(card_info.getSkill());

        txt = (TextView) findViewById(R.id.skill_details);
        if(card_info.getSkill_details()==null || card_info.getSkill_details().equalsIgnoreCase("null"))
            txt.setText("");
        else
            txt.setText(card_info.getSkill_details());

        txt = (TextView) findViewById(R.id.center_skill);
        if(card_info.getCenter_skill()==null || card_info.getCenter_skill().equalsIgnoreCase("none") || card_info.getCenter_skill().equalsIgnoreCase("null"))
            txt.setText(getString(R.string.none));
        else
            txt.setText(card_info.getCenter_skill());

        // Round Image
        im = (ImageView) findViewById(R.id.roundImage);
        card_info.showRoundImage(null, im);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
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
        public boolean onSlideLeft() {
            finish();
            overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
            return true;
        }
    }
}
