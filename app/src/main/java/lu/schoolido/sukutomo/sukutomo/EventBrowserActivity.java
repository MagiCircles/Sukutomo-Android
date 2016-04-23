package lu.schoolido.sukutomo.sukutomo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private final String EVENTS_STORED = "EventsStored";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browser);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        eventsLayout = (LinearLayout) findViewById(R.id.events_layout);
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
            EventsOpenHelper helper = new EventsOpenHelper(getApplicationContext());

            // Checking if the number of events stored to initialize the preferences:
            SharedPreferences settings = getSharedPreferences(EVENTS_STORED, MODE_PRIVATE);
            int n = settings.getInt(EVENTS_STORED, 0);
            settings.edit().putInt(EVENTS_STORED, 0);


            // A first query is made in order to get the total number of events;
            int totalEvents = APIUtils.iteratePages(null,
                    "http://schoolido.lu/api/events/?ordering=-beginning&page_size=1",
                    null,
                    1000
            );

            // We put all the new events in the "events" attribute.
            APIUtils.iteratePages(events,
                    "http://schoolido.lu/api/events/?ordering=-beginning&page_size=" + (totalEvents - n),
                    null,
                    totalEvents
            );

            try {
                for (int i = 0; i < events.size(); i++) {
                    // The event image is saved to the internal storage with a path and the
                    // event romaji name as the image file name.
                    Bitmap bm = APIUtils.getBitmap(events.get(i).getString("image"));
                    String name = events.get(i).getString("romaji_name");
                    String imagePath = APIUtils.saveToInternalStorage(getApplicationContext(), bm, "events", name);
                    Log.d("insert event images", "name: " + name);
                    Log.d("insert event images", "image: " + imagePath + "/" + name);
                    events.get(i).put("image", imagePath);
                    Log.d("insert", "i: " + i + ". name: " + name);
                    helper.insertEvent(events.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void v) {
            // The events are loaded from the database:
            EventsOpenHelper helper = new EventsOpenHelper(getApplicationContext());
            events = helper.getAllEvents();

            // The events array is iterated, adding am ImageView for each event and the
            // respective intents.
            for (int i = 0; i < events.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                String eventPath = "", eventName = "";
                try {
                    // The image local path and name are retrieve. The name used is the romaji name
                    // of the event.
                    eventPath = events.get(i).getString("image");
                    eventName = events.get(i).getString("romaji_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("event images", "name: " + eventName);
                Log.d("event images", "image: " + eventPath);

                // The event image is loaded from the database to the ImageView.
                APIUtils.loadImageFromStorage(imageView, eventPath, eventName);

                // The click listener is assigned.
                imageView.setOnClickListener(new EventButtonListener(events.get(i)));

                // The view is appended to the layout.
                eventsLayout.addView(imageView);
            }

            loadingView.clearAnimation();
            loadingView.setVisibility(View.GONE);
        }
    }

    private class EventButtonListener implements View.OnClickListener {
        private JSONObject event;

        public EventButtonListener(JSONObject obj) {
            event = obj;
        }

        @Override
        public void onClick(View v) {
            Intent eventInfo = new Intent(getApplicationContext(), EventInfoActivity.class);

            eventInfo.putExtra("event", event.toString());

            startActivity(eventInfo);
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
        }
    }
}
