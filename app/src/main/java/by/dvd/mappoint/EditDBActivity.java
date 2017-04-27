package by.dvd.mappoint;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class EditDBActivity extends AppCompatActivity {

    EditText etGroup_dialogDB, etItem_dialogDB, etDescription_dialogDB;
    TextView tvId_dialogDB;

    String idCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_db);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        idCheck = getIntent().getStringExtra("idCheck");

        etGroup_dialogDB = (EditText) findViewById(R.id.etGroup_dialogDB);
        etItem_dialogDB = (EditText) findViewById(R.id.etItem_dialogDB);
        etDescription_dialogDB = (EditText) findViewById(R.id.etDescription_dialogDB);
        tvId_dialogDB = (TextView) findViewById(R.id.tvId_dialogDB);

        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String table = "POINT_ALL";
            String[] columns = new String[] {"ITEM", "DESCRIPTION", "GROUP_ITEM"};
            String selection = "_id = ?";
            String[] selectionArgs = new String[] {idCheck};
            String groupBy = null;
            String having = null;
            String orderBy = null;
            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

            if (cursor.moveToFirst()) {
                do {
                    int itemDB = cursor.getInt(0);
                    String itemDBstr = Integer.toString(itemDB);
                    String descriptionDB = cursor.getString(1);
                    String groupDB = cursor.getString(2);

                    etGroup_dialogDB.setText(groupDB);
                    etItem_dialogDB.setText(itemDBstr);
                    etDescription_dialogDB.setText(descriptionDB);

                } while (cursor.moveToNext());

            } else {
                Toast.makeText(this, R.string.no_saved_record, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_db, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SQLiteOpenHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (id) {
            case  R.id.action_edit_db:
                if (etGroup_dialogDB.getText().toString().equals("")){
                    Toast.makeText(this, R.string.action_edit_db_1, Toast.LENGTH_SHORT).show();
                    break;
                } else if (etItem_dialogDB.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.action_edit_db_2, Toast.LENGTH_SHORT).show();
                    break;
                } else if (etGroup_dialogDB.getText().toString().equals("") || etItem_dialogDB.getText().toString().equals("")){
                    Toast.makeText(this, R.string.action_edit_db_3, Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    String groupDialogDB = etGroup_dialogDB.getText().toString();
                    String itemDialogDB = etItem_dialogDB.getText().toString();
                    String descriptDialogDB = etDescription_dialogDB.getText().toString();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ITEM", itemDialogDB);
                    contentValues.put("DESCRIPTION", descriptDialogDB);
                    contentValues.put("GROUP_ITEM", groupDialogDB);
                    db.update("POINT_ALL", contentValues, "_id=" + idCheck, null);

                    Toast.makeText(getApplicationContext(), R.string.action_edit_db_string, Toast.LENGTH_SHORT).show();
                }
                break;
            case  R.id.action_delete_string_db:
                db.delete("POINT_ALL", "_id" + "=" + idCheck, null);
                db.close();
                Toast.makeText(getApplicationContext(), R.string.action_delete_db_string, Toast.LENGTH_SHORT).show();
                break;
            case  R.id.action_canсel:
                etGroup_dialogDB.setText("");
                etItem_dialogDB.setText("");
                etDescription_dialogDB.setText("");
                break;
        }
        db.close();
        return super.onOptionsItemSelected(item);
    }

}