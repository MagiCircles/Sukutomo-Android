package lu.schoolido.sukutomo.sukutomo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
                    "romaji_name TEXT unique, " +
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

    private static final String[] columns = {
            "japanese_name",
            "romaji_name",
            "english_name",
            "image",
            "beginning",
            "end",
            "english_beginning",
            "english_end",
            "japan_current",
            "world_current",
            "N_card",
            "SR_card",
            "song"
    };

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

        try {
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
            values.put("N_card", 0);
            values.put("SR_card", 0);
            values.put("song", "");

            // Finally, the event is inserted in the database:
            // I think the best idea would be to have stored what was the last stored event, and,
            // if depending on ending date, update the event, don't do anything or insert the last one...
            // The other option is to simply update the row if the event exists, and insert it if it doesn't exist.
            if (this.getEventsByRomaji(event.getString("romaji_name")) == null) {
                db.beginTransaction();
                long inserted = db.insertOrThrow(TABLE_NAME, null, values);
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getEventsByRomaji(String romaji) {
        // First, we get the database
        SQLiteDatabase db = getWritableDatabase();

        // The events are fetched:
        db.beginTransaction();
        Cursor results = db.query(
                TABLE_NAME, // table name
                columns,    // columns
                "romaji_name LIKE \"" + romaji + '"',       // where clause
                null,       // where arguments (if there are ?s in the clause)
                null,       // group by clause
                null,       // having clause
                null        // order by clause
        );
        db.setTransactionSuccessful();
        db.endTransaction();

        // Then the data is put into the JSONObject...
        JSONObject event = null;
        try {
            if (results.getCount() > 0) {
                // ... getting the first (and unique) element in the cursor:
                results.moveToFirst();
                JSONObject object = new JSONObject();
                for (int i = 0; i < columns.length; i++) {
                    if (i == columns.length - 2 || i == columns.length - 3)
                        object.put(columns[i], results.getInt(results.getColumnIndex(columns[i])));
                    else
                        object.put(columns[i], results.getString(results.getColumnIndex(columns[i])));
                }
                event = object;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        results.close();

        return event;
    }

    public ArrayList<JSONObject> getEvents(boolean worldEvent, int page, int pageSize) {
        // First, we get the database
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = null;
        if (worldEvent) {
            whereClause = "english_beginning IS NOT NULL AND english_beginning NOT LIKE \"\" AND " +
                    "english_beginning NOT LIKE \"null\"";
        }

        // The events are fetched:
        db.beginTransaction();
        Cursor results = db.query(
                TABLE_NAME, // table name
                columns,    // columns
                whereClause,       // where clause
                null,       // where arguments (if there are ?s in the clause)
                null,       // group by clause
                null,       // having clause
                "beginning DESC"        // order by clause
        );
        db.setTransactionSuccessful();
        db.endTransaction();

        int current = (page - 1) * pageSize;
        int last = page * pageSize - 1;
        // Then the data is put into the JSONObject array...
        ArrayList<JSONObject> events = new ArrayList<>();
        try {
            if (results.getCount() > 0) {
                // ... starting from the first element in the cursor:
                results.moveToPosition(current);
                // the cursor is iterated until all elements are read.
                while (!results.isAfterLast() && current <= last) {
                    JSONObject object = new JSONObject();
                    for (int i = 0; i < columns.length; i++) {
                        if (i == columns.length - 2 || i == columns.length - 3)
                            object.put(columns[i], results.getInt(results.getColumnIndex(columns[i])));
                        else
                            object.put(columns[i], results.getString(results.getColumnIndex(columns[i])));
                    }
                    events.add(object);
                    current++;
                    results.moveToNext();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        results.close();

        return events;
    }
}
