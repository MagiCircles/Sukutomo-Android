package lu.schoolido.sukutomo.sukutomo;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class IdolsSearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {
    private ArrayList<String> idolNames;
    private ListView listView;
    private static String baseURL = "http://schoolido.lu/api/cardids/";
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idols);

        listView = (ListView) findViewById(R.id.idolsList);

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

    private class LoadIdols extends AsyncTask<String, String, Void> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(IdolsSearchActivity.this);
            pDialog.setMessage("Loading idols...");
            //pDialog.show();
        }

        protected Void doInBackground(String... args) {
            String data = "";
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                HttpClient Client = new DefaultHttpClient();
                data = Client.execute(new HttpGet("http://schoolido.lu/api/idols/"), responseHandler);
                JSONObject object = new JSONObject(data);
                int count = object.getInt("count");
                for(int i = 1; i <= Math.ceil(count/10); i++) {
                    data = Client.execute(new HttpGet("http://schoolido.lu/api/idols/?page=" + i), responseHandler);
                    object = new JSONObject(data);
                    JSONArray array = object.getJSONArray("results");
                    for(int j=0; j < array.length(); j++)
                        idolNames.add(array.getJSONObject(j).getString("name"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            pDialog.show();
            pDialog.dismiss();

            adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, idolNames);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String idolName = (String) listView.getItemAtPosition(position);

                    Intent search = new Intent(getApplicationContext(), CardBrowser.class);

                    search.putExtra("url", baseURL + "?name=" + idolName.replace(" ", "%20"));
                    startActivity(search);
                    overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
                }
            });
        }
    }
}
