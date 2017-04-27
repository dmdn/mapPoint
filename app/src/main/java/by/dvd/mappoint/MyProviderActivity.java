package by.dvd.mappoint;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class MyProviderActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvGPS, tvGPS_lat, tvGPS_lon, tvGPS_time;
    ImageView imageViewSatellite;

    Button btnSaveGPS;
    Date dateLocation;

    public String strLat, strLng;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageViewSatellite = (ImageView) findViewById(R.id.imageViewSatellite);
        tvGPS_lat = (TextView) findViewById(R.id.tvGPS_lat);
        tvGPS_lon = (TextView) findViewById(R.id.tvGPS_lon);
        tvGPS_time = (TextView) findViewById(R.id.tvGPS_time);
        tvGPS = (TextView) findViewById(R.id.tvGPS);
        btnSaveGPS = (Button) findViewById(R.id.btnSaveGPS);
        btnSaveGPS.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            simpleDialog();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            imageViewSatellite.setImageResource(R.drawable.signal_off);
            Toast.makeText(MyProviderActivity.this, R.string.provider_off, Toast.LENGTH_SHORT).show();
            simpleDialog();
        }

        @Override
        public void onProviderEnabled(String provider) {
            imageViewSatellite.setImageResource(R.drawable.signal_off);
            Toast.makeText(MyProviderActivity.this, R.string.provider_on, Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(MyProviderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyProviderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == 0 ) {
                imageViewSatellite.setImageResource(R.drawable.signal_off);
                Toast.makeText(MyProviderActivity.this, R.string.provider_1, Toast.LENGTH_LONG).show();
            } else if (status == 1) {
                imageViewSatellite.setImageResource(R.drawable.signal_off);
                Toast.makeText(MyProviderActivity.this, R.string.provider_2, Toast.LENGTH_LONG).show();
            } else if (status == 2) {
                imageViewSatellite.setImageResource(R.drawable.gps_on);
                Toast.makeText(MyProviderActivity.this, R.string.provider_3, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null){
            imageViewSatellite.setImageResource(R.drawable.signal_off);
            return;
        } else  {
            imageViewSatellite.setImageResource(R.drawable.gps_on);
            strLat = Double.toString(location.getLatitude());
            strLng = Double.toString(location.getLongitude());
            dateLocation = new Date(location.getTime());
            tvGPS.setText(R.string.coordinates);
            String latFormat = getString(R.string.format_lat);
            String lonFormat = getString(R.string.format_lon);
            String timeFormat = getString(R.string.format_time);
            tvGPS_lat.setText(String.format(latFormat, location.getLongitude()));
            tvGPS_lon.setText(String.format(lonFormat, location.getLatitude()));
            tvGPS_time.setText(String.format(timeFormat, dateLocation));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveGPS:
                if (strLat == null && strLng == null) {
                    return;
                }else {
                    MarkerInfoWindowActivity.DATE_LOCATION = dateLocation;
                    Intent intent = new Intent(this, MarkerInfoWindowActivity.class);
                    intent.putExtra("lat", strLat);
                    intent.putExtra("lng", strLng);
                    startActivity(intent);
                }
                break;
        }

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