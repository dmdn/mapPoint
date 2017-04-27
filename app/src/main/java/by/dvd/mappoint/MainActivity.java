package by.dvd.mappoint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Drawer drawerResult;

    public static  List<CheckBox> chBox;

    private List<Point> points;
    private RecyclerView rv;

    public static String ID_DB_ONCLICK;

    Set<String> itemsSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            initializeNavigationDrawer(toolbar);
        }

        final FloatingActionButton actionDelete_db = (FloatingActionButton) findViewById(R.id.action_menu_delete_db);
        actionDelete_db.setOnClickListener(this);
        final FloatingActionButton actionEditDB = (FloatingActionButton) findViewById(R.id.action_menu_editDB);
        actionEditDB.setOnClickListener(this);
        final FloatingActionButton actionAutorenewDB = (FloatingActionButton) findViewById(R.id.action_menu_autorenewDB);
        actionAutorenewDB.setOnClickListener(this);

        itemsSet = new HashSet<>();

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager sglManager = new LinearLayoutManager(this);
        rv.setLayoutManager(sglManager);
        rv.setHasFixedSize(true);

        chBox = new ArrayList<>();

        initializeData();
        initializeAdapter();
    }


    private void initializeData(){
        points = new ArrayList<>();

        try {
            SQLiteOpenHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String table = "POINT_ALL";
            String[] columns = null;
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = null;
            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

            if (cursor.moveToFirst()) {
                do {
                    int idDB = cursor.getInt(0);
                    String idDBstr = Integer.toString(idDB);
                    double latDB = cursor.getDouble(1);
                    double lngDB = cursor.getDouble(2);
                    Date dateDB = new Date(cursor.getLong(3));
                    String alert3 = getString(R.string.format_time);
                    String dateDBstr = String.format(alert3, dateDB);
                    int itemDB = cursor.getInt(4);
                    String itemDBstr = Integer.toString(itemDB);
                    String descriptionDB = cursor.getString(5);
                    String groupDB = cursor.getString(6);

                    points.add(new Point(groupDB, itemDBstr, descriptionDB, R.drawable.img_poit, idDBstr, dateDBstr));
                    itemsSet.add(groupDB);

                } while (cursor.moveToNext());
            } else {
                String alert1 = getString(R.string.alert_error_1);
                points.add(new Point("", "", alert1, R.drawable.wrong, "", ""));
            }
            cursor.close();
            db.close();
            dbHelper.close();
        } catch(SQLiteException e) {
            String alert2 = getString(R.string.alert_error_2);
            points.add(new Point("", "", alert2, R.drawable.error, "", ""));
            Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(points);
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_menu_delete_db:
                SQLiteOpenHelper dbHelper = new DBHelper(MainActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("POINT_ALL", null, null);
                db.close();
                dbHelper.close();
                Toast.makeText(MainActivity.this, R.string.deleting, Toast.LENGTH_SHORT).show();

                initializeData();
                initializeAdapter();
                break;
            case R.id.action_menu_editDB:
                if (ID_DB_ONCLICK != null){
                    Intent intent2 = new Intent(this, EditDBActivity.class);
                    intent2.putExtra("idCheck", ID_DB_ONCLICK);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, R.string.select_string_change, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_menu_autorenewDB:
                initializeData();
                initializeAdapter();
                break;
        }
    }


    @Override
    public void onBackPressed(){
        if (drawerResult != null && drawerResult.isDrawerOpen()){
            drawerResult.closeDrawer();
        }else {
            super.onBackPressed();
        }
    }


    private AccountHeader createAccountHeader() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.backgraund_material)
                .build();
    }


    private void initializeNavigationDrawer(Toolbar toolbar) {

        AccountHeader accHeaderResult = createAccountHeader();

        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accHeaderResult)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withSliderBackgroundColor(Color.parseColor("#e0e0e0"))

                .addDrawerItems(initialiseDrawerItems())

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch((int) drawerItem.getIdentifier()){
                            case 1:
                                Intent intent1 = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent1);
                                break;
                            case 2:
                                Intent intent2 = new Intent(MainActivity.this, MapActivity.class);
                                startActivity(intent2);
                                break;
                            case 3:
                                Intent intent3 = new Intent(MainActivity.this, MyProviderActivity.class);
                                startActivity(intent3);
                                break;
                            case 4:
                                final String[] items = itemsSet.toArray(new String[itemsSet.size()]);
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.list_dialog_title)
                                        .setIcon(android.R.drawable.ic_dialog_map)
                                        .setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(getApplicationContext(),items[i], Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(MainActivity.this, ShowMyPointsMapActivity.class);
                                                intent.putExtra("group", items[i]);
                                                startActivity(intent);
                                            }
                                        })
                                        .create()
                                        .show();
                                break;
                            case 5:
                                Intent intent5 = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent5);
                                break;
                        }
                        return true;
                    }
                })
                .build();

    }

    private IDrawerItem[] initialiseDrawerItems() {

        return new IDrawerItem[]{
                new PrimaryDrawerItem()
                        .withName(R.string.nav_menu_item_data)
                        .withIdentifier(1)
                        .withIcon(R.drawable.ic_description_black_18dp),

                new PrimaryDrawerItem()
                        .withName(R.string.nav_menu_item_map)
                        .withIdentifier(2)
                        .withIcon(R.drawable.ic_map_black_18dp),

                new PrimaryDrawerItem()
                        .withName(R.string.nav_menu_item_gps)
                        .withIdentifier(3)
                        .withIcon(R.drawable.ic_my_location_black_18dp),

                new PrimaryDrawerItem()
                        .withName(R.string.nav_menu_item_position)
                        .withIdentifier(4)
                        .withIcon(R.drawable.ic_pin_drop_black_18dp),

                new DividerDrawerItem(),

                new SectionDrawerItem()
                        .withName(R.string.nav_menu_other)
                        .withDivider(false),

                new SecondaryDrawerItem()
                        .withName(R.string.nav_menu_item_settings)
                        .withIdentifier(5)
                        .withIcon(R.drawable.ic_settings_black_18dp)
        };
    }



}
