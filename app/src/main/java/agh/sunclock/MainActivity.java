package agh.sunclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import client.IPGeolocationClient;
import model.SunData;

public class MainActivity extends AppCompatActivity {
    private IPGeolocationClient client;
    private Double latitude;
    private Double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initClient();
    }

    public void click(View view) {
        SunData sunData = client.getSunInfo(latitude, longitude);
        setLabel(sunData.toString());
    }

    private void initClient() {
        client = new IPGeolocationClient(this);
    }

    private void setLabel(String text) {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(e -> ((TextView) findViewById(R.id.textView)).setText(text));
    }

    private void setLabelGPS(String text) {
        Button button = (Button) findViewById(R.id.gpsButton);
        button.setOnClickListener(e -> ((TextView) findViewById(R.id.latText)).setText(text));
    }

    public void gpsDataReader(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                System.out.println(String.valueOf(location.getLatitude()));
                setLabelGPS("LNG: " + String.valueOf(location.getLongitude()) + " \nLAT: " + String.valueOf(latitude));
            }
        });
    }
}
