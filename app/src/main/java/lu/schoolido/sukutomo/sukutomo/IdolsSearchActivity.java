package lu.schoolido.sukutomo.sukutomo;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;


public class IdolsSearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {
    private ArrayList<String> idolNames;
    private ListView listView;
    private static String baseURL = "http://schoolido.lu/api/cardids/";
    private IdolsAdapter adapter;
    private ImageView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idols);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView = (ListView) findViewById(R.id.idolsList);
        loadingView = (ImageView) findViewById(R.id.loading_view);
        loadingView.setVisibility(View.GONE);

        idolNames = new ArrayList<>();
        LoadIdols li = new LoadIdols();
        li.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_idols, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStop() {
        super.onStop();
        onTrimMemory(TRIM_MEMORY_RUNNING_MODERATE);
    }

    private class LoadIdols extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingView.setVisibility(View.VISIBLE);
            loadingView.setAnimation(CardBrowser.loadAnimation);
        }

        protected Void doInBackground(String... args) {
            APIUtils.iteratePages(idolNames, "http://schoolido.lu/api/idols/", "name", 10, 0);
            return null;
        }

        protected void onPostExecute(Void v) {
            adapter = new IdolsAdapter(getApplicationContext(),
                    R.layout.menu_list_item, idolNames);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String idolName = (String) listView.getItemAtPosition(position);

                    Intent search = new Intent(getApplicationContext(), CardBrowser.class);

                    search.putExtra("url", baseURL + "?name=" + idolName.replace(" ", "%20"));
                    startActivity(search);
                    overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
                }
            });
            loadingView.clearAnimation();
            loadingView.setVisibility(View.GONE);
        }
    }
}
