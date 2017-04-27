package by.dvd.mappoint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;


public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, LocationListener,
        GoogleMap.OnInfoWindowClickListener {


    private GoogleMap mMap;
    private LocationManager mLocationManager;
    public String strLat, strLng;
    Date dateLocation;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        switch (preferences.getString(getString(R.string.pref_key_map_type), "")) {
            case "scheme":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "terrain":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }

        mMap.setOnInfoWindowClickListener(this);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }


        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            simpleDialog();
        } else {
            mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        }


    }


    @Override
    public void onLocationChanged(Location location) {
        strLat = Double.toString(location.getLatitude());
        strLng = Double.toString(location.getLongitude());

        dateLocation = new Date(location.getTime());
        String timeFormat = getString(R.string.format_time);
        String dateStr = String.format(timeFormat, dateLocation);

        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions();
        marker.position(pos);

        String imHere = getString(R.string.im_here);
        marker.title(imHere);
        marker.snippet(strLat + "; " + strLng + "; " + dateStr);
        marker.draggable(true);
        mMap.addMarker(marker);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pos, 12);
        mMap.animateCamera(update);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        MarkerInfoWindowActivity.DATE_LOCATION = dateLocation;
        Intent intent = new Intent(this, MarkerInfoWindowActivity.class);
        intent.putExtra("lat", strLat);
        intent.putExtra("lng", strLng);
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void simpleDialog() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case Dialog.BUTTON_POSITIVE:
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), R.string.cancel, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_location_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.alert_location_message)
                .setNegativeButton(R.string.alert_location_btn_cancel, onClickListener)
                .setPositiveButton(R.string.alert_location_btn_ok, onClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
