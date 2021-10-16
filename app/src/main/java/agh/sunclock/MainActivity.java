package agh.sunclock;

import androidx.annotation.RequiresApi;
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
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Optional;

import model.SunData;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    private Optional<Double> latitude = Optional.empty();
    private Optional<Double> longitude = Optional.empty();
    private SunData sunData;

    // new code that read data about smartphones tilt around each axis
    // floatOrientation -> matrix containing this data
    // [0] -> z axis (North = 0)
    // [1] -> x axis (Camera faces sky/ground) tilt upper and  bottom edge
    // [2] -> y axis tilt left or right edge

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    private ImageView imageView;
    private ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinner);
        init();
    }

    public Optional<Double> getLatitude() {
        return latitude;
    }

    public Optional<Double> getLongitude() {
        return longitude;
    }

    public void changeToMainView() {
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.dial);
        imageView3 = findViewById(R.id.shadow);
        initSensorManager();
    }

    public void setSunData(SunData sunData) {
        this.sunData = sunData;
    }

    private void init() {
        readGpsData();
        new GetSunDataAsyncTask(this).execute(latitude, longitude);
    }

    public void updateLocalizationData(View view) {
        setContentView(R.layout.spinner);
        new GetSunDataAsyncTask(this).execute(latitude, longitude);
    }

    private void readGpsData() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, this::handleLocationChanged);
    }

    private void handleLocationChanged(Location location) {
        latitude = Optional.of(location.getLatitude());
        longitude = Optional.of(location.getLongitude());
    }

    private void initSensorManager() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListenerAccelerometer = getAccelerometerSensorListener();
        SensorEventListener sensorEventListenerMagneticField = getMagneticSensorListener();

        sensorManager.registerListener(sensorEventListenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorEventListener getAccelerometerSensorListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGravity = event.values;
                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

                imageView.setRotation((float) (floatOrientation[0] * 180 / 3.1415));
                double compass = floatOrientation[0] * 180 / 3.1415;
                imageView3.setRotation((float) (sunData.getAzimuth().floatValue()) - 180 + (float) compass);
                if (sunData.getAltitude() < 0) {
                    imageView3.setScaleY(0);
                } else {
                    imageView3.setScaleY((float) (1 / Math.tan(sunData.getAltitude().floatValue())));
                }
                System.out.println("altitude: " + sunData.getAltitude());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    private SensorEventListener getMagneticSensorListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGeoMagnetic = event.values;
                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

                imageView.setRotation((float) (floatOrientation[0] * 180 / 3.1415));
                double compass = floatOrientation[0] * 180 / 3.1415;
                imageView3.setRotation((float) (sunData.getAzimuth().floatValue()) - 180 + (float) compass);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }
}


