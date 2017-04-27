package by.dvd.mappoint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "map_point";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {super(context, DB_NAME, null, DB_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE POINT_ALL (_id INTEGER PRIMARY KEY, "
                + "LATITUDE REAL, "
                + "LONGITUDE REAL, "
                + "DATE INTEGER, "
                + "ITEM INTEGER, "
                + "DESCRIPTION TEXT, "
                + "GROUP_ITEM TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RESULTS");
        onCreate(db);
    }
}