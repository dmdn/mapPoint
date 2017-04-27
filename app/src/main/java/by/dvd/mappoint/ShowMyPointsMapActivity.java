package by.dvd.mappoint;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;


public class ShowMyPointsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public String group;
    private GoogleMap pointMap;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snow_my_points_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        group = getIntent().getStringExtra("group");

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pointMap);
        supportMapFragment.getMapAsync(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        pointMap = googleMap;
        pointMap.getUiSettings().setZoomControlsEnabled(true);
        pointMap.getUiSettings().setMyLocationButtonEnabled(true);

        switch (preferences.getString(getString(R.string.pref_key_map_type), "")) {
            case "scheme":
                pointMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "satellite":
                pointMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "terrain":
                pointMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                pointMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }

        pointMap.setMyLocationEnabled(false);

        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String table = "POINT_ALL";
            String[] columns = new String[] {"LATITUDE", "LONGITUDE", "DATE", "ITEM", "GROUP_ITEM"};
            String selection = "GROUP_ITEM = ?";
            String[] selectionArgs = new String[] {group};
            String groupBy = null;
            String having = null;
            String orderBy = null;
            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

            if (cursor.moveToFirst()) {
                do {
                    double latDB = cursor.getDouble(0);
                    double lngDB = cursor.getDouble(1);
                    Date dateDB = new Date(cursor.getLong(2));
                    String alert = getString(R.string.format_time);
                    String dateDBstr = String.format(alert, dateDB);
                    int itemDB = cursor.getInt(3);
                    String groupDB = cursor.getString(4);

                    LatLng pos = new LatLng(latDB, lngDB);
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(pos);
                    marker.title("№ " + Integer.toString(itemDB) + " - " + groupDB);
                    marker.snippet(dateDBstr);
                    pointMap.addMarker(marker);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pos, 10);
                    pointMap.animateCamera(update);

                } while (cursor.moveToNext());

            } else {
                Toast toast = Toast.makeText(this, R.string.no_saved_record, Toast.LENGTH_SHORT);
                toast.show();
            }
            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_SHORT);
            toast.show();
        }

    }


}