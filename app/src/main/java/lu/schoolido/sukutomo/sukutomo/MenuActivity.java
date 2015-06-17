package lu.schoolido.sukutomo.sukutomo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MenuActivity extends Activity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listView = (ListView) findViewById(R.id.menu_list);
        String[] options = new String[]{
                getString(R.string.Login), getString(R.string.Cards), getString(R.string.Idols),
                getString(R.string.Events), getString(R.string.Contest), getString(R.string.Players),
                "Surprise no. 1", "Surprise no. 2"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, options);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
                        break; // Log in screen
                    case 1: finish(); // go to the CardBrowser
                            break;
                    case 2: break; // go to idols list
                    case 3: break; // go to browser view for events
                    case 4: break; // go to browser view for the contest
                    case 5: break; // go to browser view for users
                    case 6: break; // go to ...
                    case 7: break; // go to ...
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
