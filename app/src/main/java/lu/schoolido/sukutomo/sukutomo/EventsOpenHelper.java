package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arukantara on 18/10/15.
 */
public class EventsOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SukuTomo";
    private static final int DATABASE_VERSION = 2;
    private static final String DICTIONARY_TABLE_NAME = "events";
    // https://www.sqlite.org/datatype3.html
    // the dates will be stored as text, but the JSON format used is:
    // YYYY-MM-DDTHH:MM:SS+HH:MM, where T is the letter "T" and
    // the secong HH:MM part is the japanase UTC time offset.
    // The SQLite format is:
    // YYYY-MM-DD HH:MM:SS.SSS
    // I have to manage the notifications taking into account the offset.
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    "japanese_name TEXT, " +
                    "romaji_name TEXT, " +
                    "english_name TEXT, " +
                    "image TEXT, " +
                    "beginning TEXT, " +
                    "end TEXT, " +
                    "english_beginning TEXT, " +
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
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
