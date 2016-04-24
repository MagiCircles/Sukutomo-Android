package lu.schoolido.sukutomo.sukutomo;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class EventBrowserActivity extends AppCompatActivity {
    private ArrayList<JSONObject> events;
    private LinearLayout eventsLayout;
    private static String baseURL = "http://schoolido.lu/api/cardids/";
    private ImageView loadingView;
    private final String EVENTS_STORED = "EventsStored";
    private int page = 1;
    private int pageSize = 5;
    private boolean worldEvent = false;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browser);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        eventsLayout = (LinearLayout) findViewById(R.id.events_layout);
        loadingView = (ImageView) findViewById(R.id.loading_view);
        loadingView.setVisibility(View.GONE);
        Intent intent = getIntent();
        page = intent.getIntExtra("page", 1);
        worldEvent = intent.getBooleanExtra("worldEvent", false);

        //Preparing page buttons:
        Button previousButton = (Button) findViewById(R.id.previousPage);
        if (page > 1) {
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = getIntent();
                    newIntent.putExtra("page", page - 1);
                    newIntent.putExtra("worldEvent", worldEvent);
                    finish();
                    startActivity(newIntent);
                    overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
                }
            });
        } else {
            previousButton.setVisibility(View.INVISIBLE);
        }
        Button nextButton = (Button) findViewById(R.id.nextPage);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = getIntent();
                newIntent.putExtra("page", page + 1);
                newIntent.putExtra("worldEvent", worldEvent);
                finish();
                startActivity(newIntent);
                overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
            }
        });
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = getIntent();
                newIntent.putExtra("page", 1);
                newIntent.putExtra("worldEvent", !worldEvent);
                finish();
                startActivity(newIntent);
            }
        });
        toggleButton.setChecked(worldEvent);

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
            ArrayList lastEventDate = new ArrayList();
            // A first query is made in order to get the total number of events;
            int totalEvents = APIUtils.iteratePages(lastEventDate,
                    "http://schoolido.lu/api/events/?ordering=-beginning&page_size=1",
                    "beginning",
                    1,
                    1
            );
            int storedEvents = helper.getStoredEventsCount();
            String lastSavedDate = helper.getLastEventDate();
            String date = (String) lastEventDate.get(0);
            // If the database is not fully updated, import current page events directly from
            // the API and stored them in the database:
            if (!lastSavedDate.equalsIgnoreCase(date)) {
                // First download:
                if (storedEvents == 0) {
                    int totalPages = (int) Math.ceil(totalEvents / 15);
                    for (int i = 1; i <= totalPages + 1; i++) {
                        APIUtils.getPage(events, "http://schoolido.lu/api/events/?ordering=-beginning", null,
                                15, i);
                    }
                } else {
                    APIUtils.getPage(events, "http://schoolido.lu/api/events/?ordering=-beginning", null,
                            pageSize, page);
                }
                Log.d("DEBUG", "Events retrieved count: " + events.size());
                try {
                    for (int i = 0; i < events.size(); i++) {
                        // The event image is saved to the internal storage with a path and the
                        // event romaji name as the image file name.
                        Bitmap bm = APIUtils.getBitmap(events.get(i).getString("image"));
                        String name = events.get(i).getString("romaji_name");
                        if (name.equals("")) {
                            name = events.get(i).getString("japanses_name");
                        }
                        String imagePath = APIUtils.saveToInternalStorage(getApplicationContext(), bm, "events", name);
                        events.get(i).put("image", imagePath);
                        helper.insertEvent(events.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(Void v) {
            // The events are loaded from the database:
            EventsOpenHelper helper = new EventsOpenHelper(getApplicationContext());
            events = helper.getEvents(worldEvent, page, pageSize);
            // If there are no events, we have surpassed the first/last page:
            if (events.size() <= 0) {
                Intent newIntent = getIntent();
                newIntent.putExtra("page", 1);
                newIntent.putExtra("worldEvent", worldEvent);
                finish();
            }

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
            Intent act = new Intent(getApplicationContext(), WebActivity.class);
            String url = null;
            try {
                url = "http://schoolido.lu/events/" + event.getString("japanese_name") + "/";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            act.putExtra("url", url);
            startActivity(act);
        }
    }
}
