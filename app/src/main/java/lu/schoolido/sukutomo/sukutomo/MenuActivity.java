package lu.schoolido.sukutomo.sukutomo;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MenuActivity extends ActionBarActivity {
    public static final float CURRENT_VERSION = 0.9f;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView = (ListView) findViewById(R.id.menu_list);
        String[] options = new String[]{
                /*getString(R.string.Login), */
                getString(R.string.Cards), getString(R.string.Idols),
                getString(R.string.Events), getString(R.string.Contest), getString(R.string.Players),
                getString(R.string.ContactUs), getString(R.string.CheckForUpdates), "???"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.menu_list_item, android.R.id.text1, options);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    /*case 0:
                        startActivity(LoginActivity.class, null);
                        break; // Log in screen*/
                    case 0:
                        finish(); // go to the CardBrowser
                        break;
                    case 1:
                        startActivity(IdolsSearchActivity.class, null);
                        break; // go to idols list
                    case 2:
                        startActivity(WebActivity.class, "http://schoolido.lu/events/?hidenavbar");
                        break; // go to browser view for events
                    case 3:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://schoolido.lu/contest/"));
                        startActivity(browserIntent);
                        break; // go to browser view for the contest
                    case 4:
                        startActivity(WebActivity.class, "http://schoolido.lu/users/?hidenavbar");
                        break; // go to browser view for users
                    case 5:
                        AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this).create(); //Read Update
                        alertDialog.setTitle(getString(R.string.ContactUs));
                        alertDialog.setMessage(getString(R.string.ContactText) + "\n\n" + getString(R.string.contactEmail));
                        alertDialog.setButton(getString(R.string.SendEmail), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "arukantara@schoolido.lu", null));
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                intent.putExtra(Intent.EXTRA_TEXT, "");
                                startActivity(Intent.createChooser(intent, getString(R.string.ChooseEmailClient)));
                            }
                        });

                        alertDialog.show();
                        break;
                    case 6:
                        UpdateChecker uc = new UpdateChecker();
                        uc.execute();
                        break;
                    case 7:
                        Toast toast = Toast.makeText(getApplicationContext(), "Be patient, we'll develop this soon...", Toast.LENGTH_SHORT);
                        toast.show();
                        break; // go to ...
                }

            }
        });
    }

    private void startActivity(Class cls, String url) {
        Intent act = new Intent(getApplicationContext(), cls);
        if(url != null)
            act.putExtra("url", url);
        startActivity(act);
        //overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    /**
     * Class used to check if there are any pending updates.
     */
    private class UpdateChecker extends AsyncTask<Void, Void, Void> {
        private Toast downloadingToast;
        private Toast noUpdatesToast;
        private Toast failToast;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            downloadingToast = Toast.makeText(getApplicationContext(), R.string.DownloadingUpdate, Toast.LENGTH_LONG);
            noUpdatesToast = Toast.makeText(getApplicationContext(), getString(R.string.NoUpdates) + " v" + CURRENT_VERSION, Toast.LENGTH_LONG);
            failToast = Toast.makeText(getApplicationContext(), R.string.ConnectionFailed, Toast.LENGTH_LONG);
            progressDialog = new ProgressDialog(MenuActivity.this);
            progressDialog.setMessage(getString(R.string.CheckingUpdates));
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Preparing the URL used to get the current app version:
                URL url = new URL("http://schoolido.lu/api/app/android/");

                // Stablishing connection:
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Reading the response:
                // System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                // TODO find why this doesn't work.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = bufferedReader.readLine().trim();
                JSONObject jsonObject = new JSONObject(response);
                double newVersion = jsonObject.getDouble("version");
                if (newVersion > CURRENT_VERSION) {
                    // If there is a new version, we cancel the dialog and show the toast:
                    downloadingToast.show();
                    progressDialog.cancel();
                    // Then, we construct the URI for the download link...
                    String downloadUrl = jsonObject.getString("download_link").trim();
                    // ... and pass it to the download manager request:
                    DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadUrl));

                    // We prepare the download and add it to the queue.
                    downloadRequest.setDescription(getString(R.string.DownloadingUpdate) + " Sukutomo v" + newVersion);
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.enqueue(downloadRequest);
                } else {
                    noUpdatesToast.show();
                    progressDialog.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
                failToast.show();
                progressDialog.cancel();
            }
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }
}
