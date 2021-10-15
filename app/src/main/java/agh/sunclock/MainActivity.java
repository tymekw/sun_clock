package agh.sunclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

    private SunData sunData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void setSunData(SunData sunData) {
        this.sunData = sunData;
    }

    public void click(View view) {
        setLabel(sunData.toString());
    }

    private void init() {
        initClient();
        readGpsData();
        new GetSunDataAsyncTask(this).execute(latitude, longitude);
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
        readGpsData();
    }

    private void readGpsData() {
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


    // new code that read data about smartphones tilt around each axis
    // floatOrientation -> matrix containing this data
    // [0] -> z axis (North = 0)
    // [1] -> x axis (Camera faces sky/ground) tilt upper and  bottom edge
    // [2] -> y axis tilt left or right edge

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];


    public void onSensorClick(String text) {
        Button button = (Button) findViewById(R.id.orientation);
        button.setOnClickListener((e -> ((TextView) findViewById(R.id.latText)).setText(text)));
    }

    public void SensorManager(View view) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListenerAccelrometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGravity = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
                onSensorClick("ok");
                System.out.println("N/S: " + floatOrientation[0] +
                        "\n screen up/down: " + floatOrientation[1] +
                        "\n screen left/right: " + floatOrientation[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGeoMagnetic = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
                onSensorClick("ok1");
                System.out.println("Magnetic N/S: " + floatOrientation[0] +
                        "\n Magnetic screen up/down: " + floatOrientation[1] +
                        "\n Magnetic screen left/right: " + floatOrientation[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListenerAccelrometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }
}


