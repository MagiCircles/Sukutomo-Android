package lu.schoolido.sukutomo.sukutomo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arukantara on 18/10/15.
 */
public class EventsOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SukuTomo";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "events";
    // https://www.sqlite.org/datatype3.html
    // the dates will be stored as text, but the JSON format used is:
    // YYYY-MM-DDTHH:MM:SS+HH:MM, where T is the letter "T" and
    // the secong HH:MM part is the japanase UTC time offset.
    // The SQLite format is:
    // YYYY-MM-DD HH:MM:SS.SSS
    // I have to manage the notifications taking into account the offset.
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "japanese_name TEXT, " +
                    "romaji_name TEXT, " +
                    "english_name TEXT, " +
                    "image TEXT, " +
                    "beginning TEXT, " +
                    "end TEXT, " +
                    "english_beginning TEXT, " +
                    "english_end TEXT, " +
                    "japan_current INTEGER, " +
                    "world_current INTEGER, " +
                    "N_card INTEGER, " +
                    "SR_card INTEGER, " +
                    "song TEXT" +
                    ");";

    EventsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Inserts a new event in the database.
     *
     * @param event JSONObject contained by the events JSONArray, which contains the desired event.
     */
    public void insertEvent(JSONObject event) {
        // First, we get the database
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();


        JSONArray cards = null;
        try {
            // we get the cards from the JSON Object. The first one is the N card, and the second
            // one is the SR card.
            cards = event.getJSONArray("cards");

            // we put all the needed values in the ContentValues container
            values.put("japanese_name", event.getString("japanese_name"));
            values.put("romaji_name", event.getString("romaji_name"));
            values.put("english_name", event.getString("english_name"));
            values.put("image", event.getString("image"));
            values.put("beginning", event.getString("beginning"));
            values.put("end", event.getString("end"));
            values.put("english_beginning", event.getString("english_beginning"));
            values.put("english_end", event.getString("english_end"));
            values.put("japan_current", event.getBoolean("japan_current"));
            values.put("world_current", event.getBoolean("world_current"));
            values.put("N_card", cards.getInt(0));
            values.put("SR_card", cards.getInt(1));
            values.put("song", event.getString("song"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Finally, the event is inserte in the database:
        db.beginTransaction();
        db.insert(TABLE_NAME, null, values);
        db.endTransaction();
    }
}
