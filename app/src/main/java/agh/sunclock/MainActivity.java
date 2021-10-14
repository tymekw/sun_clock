package agh.sunclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.lang.Object;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    Double latitude;
    Double longitude;

    Double sunAltitude;
    Double sunAzimuth;
    Double sunDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.ipgeolocation.io/astronomy?apiKey=b11b04e7b7a546c6b01ea20c3d79fb7e&lat=41.3558443&long=-74.00776718841271";

        // Display the first 500 characters of the response string.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    parseResponse(response);
                }, error -> {
            setLabel("That didn't work!");
        });
        queue.add(stringRequest);
    }

    private void parseResponse(String response){

        try {
            JSONObject jObject = new JSONObject(response);
            sunAltitude = jObject.getDouble("sun_altitude");
            sunAzimuth = jObject.getDouble("sun_azimuth");
            sunDistance = jObject.getDouble("sun_distance");
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


    private void setLabel(String text) {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(e -> ((TextView) findViewById(R.id.textView)).setText(text));
    }


    private void setLabelGPS(String text) {
        Button button = (Button) findViewById(R.id.gpsButton);
        button.setOnClickListener(e -> ((TextView) findViewById( R.id.latText)).setText(text));
    }

    public void gpsDataReader(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
