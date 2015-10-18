package lu.schoolido.sukutomo.sukutomo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventBrowserActivity extends AppCompatActivity {
    private ArrayList<JSONObject> events;
    private LinearLayout eventsLayout;
    private static String baseURL = "http://schoolido.lu/api/cardids/";
    private ImageView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browser);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        eventsLayout = (LinearLayout) findViewById(R.id.eventsList);
        loadingView = (ImageView) findViewById(R.id.loading_view);
        loadingView.setVisibility(View.GONE);

        events = new ArrayList<>();
        LoadEvents le = new LoadEvents();
        le.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_browser, menu);
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

    private class LoadEvents extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingView.setVisibility(View.VISIBLE);
            loadingView.setAnimation(CardBrowser.loadAnimation);
        }

        protected Void doInBackground(String... args) {
            // We put all events in the "events" attribute.
            APIUtils.iteratePages(events,
                    "http://schoolido.lu/api/events/?page_size=1000",
                    null,
                    1000
            );

            return null;
        }

        protected void onPostExecute(Void v) {
            // The events array is iterated, adding am ImageView for each event and the
            // respective intents.
            for (int i = 0; i < events.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                String eventURL = "";
                try {
                    eventURL = events.get(i).getString("image");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                APIUtils utils = new APIUtils();
                utils.loadImage(imageView, eventURL);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent eventInfo = new Intent(getApplicationContext(), EventInfoActivity.class);

                        startActivity(eventInfo);
                        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
                    }
                });
                eventsLayout.addView(imageView);
            }

            loadingView.clearAnimation();
            loadingView.setVisibility(View.GONE);
        }
    }
}
