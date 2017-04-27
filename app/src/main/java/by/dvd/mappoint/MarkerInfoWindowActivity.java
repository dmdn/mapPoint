package by.dvd.mappoint;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class MarkerInfoWindowActivity extends AppCompatActivity {

    public static Date DATE_LOCATION;

    SharedPreferences sPref;
    String SAVED_GROUP_ITEM = "saved_group_item";
    String SAVED_ITEM = "saved_item";
    //String SAVED_ITEM_ONDESTROY = "saved_item_onDestroy";

    String selectItemContextMenuText = "Привет";

    int i = 1;

    public String lat, lng;

    TextView tvLat, tvLng, tvDate;
    EditText etGroup, etItem, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info_window);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");

        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLat.setText(lat);
        tvLng = (TextView) findViewById(R.id.tvLng);
        tvLng.setText(lng);
        tvDate = (TextView) findViewById(R.id.tvDate);

        String timeFormat = getString(R.string.format_time);
        tvDate.setText(String.format(timeFormat, DATE_LOCATION));

        etGroup = (EditText) findViewById(R.id.etGroup);
        etItem = (EditText) findViewById(R.id.etItem);

        etDescription = (EditText) findViewById(R.id.etDescription);

        registerForContextMenu(etGroup);

        if (etItem.getText().toString().isEmpty()) {
            etItem.setText(Integer.toString(i));
        }
    }


    private void saveText(String saved, EditText et) {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(saved, et.getText().toString());
        ed.commit();
    }

    private void loadText(String saved, EditText et) {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedText = sPref.getString(saved, "");
        et.setText(savedText);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_GROUP_ITEM, "");
        menu.add(0, 0, 0, savedText);
        selectItemContextMenuText = savedText;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        etGroup.setText(selectItemContextMenuText);
        loadText(SAVED_ITEM, etItem);
        i += Integer.valueOf(etItem.getText().toString());
        etItem.setText(Integer.toString(i));
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marker_info_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case  R.id.action_save:
                if (etGroup.getText().toString().equals("")){
                    Toast.makeText(this, R.string.action_edit_db_1, Toast.LENGTH_SHORT).show();
                    break;
                } else if (etItem.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.action_edit_db_2, Toast.LENGTH_SHORT).show();
                    break;
                } else if (etGroup.getText().toString().equals("") || etItem.getText().toString().equals("")){
                    Toast.makeText(this, R.string.action_edit_db_3, Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    saveText(SAVED_GROUP_ITEM, etGroup);
                    saveText(SAVED_ITEM, etItem);

                    SQLiteOpenHelper dbHelper = new DBHelper(this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    String description = etDescription.getText().toString();
                    long date = DATE_LOCATION.getTime();
                    String group = etGroup.getText().toString();
                    int it = Integer.valueOf(etItem.getText().toString());

                    contentValues.put("LATITUDE", lat);
                    contentValues.put("LONGITUDE", lng);
                    contentValues.put("DATE", date);
                    contentValues.put("ITEM", it);
                    contentValues.put("DESCRIPTION", description);
                    contentValues.put("GROUP_ITEM", group);

                    db.insert("POINT_ALL", null, contentValues);

                    Toast.makeText(this, R.string.recording_saved, Toast.LENGTH_SHORT).show();

                    etGroup.setText("");
                    etDescription.setText("");
                    etItem.setText(Integer.toString(i = 1));
                }
                break;
            case  R.id.action_clear:
                etGroup.setText("");
                etDescription.setText("");
                etItem.setText(Integer.toString(i = 1));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}