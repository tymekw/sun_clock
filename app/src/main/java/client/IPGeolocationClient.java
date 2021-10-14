package client;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import model.SunData;

public class IPGeolocationClient {
    private static final String API_KEY = "b11b04e7b7a546c6b01ea20c3d79fb7e";
    private static final String IP_GEOLOCATION_ASTRONOMY_URL = "https://api.ipgeolocation.io/astronomy";

    private final Context context;

    public IPGeolocationClient(Context context) {
        this.context = context;
    }

    public SunData getSunInfo(Double lat, Double lon) {
        RequestFuture<String> future = RequestFuture.newFuture();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = buildUrl(lat.toString(), lon.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, future, future);
        queue.add(stringRequest);
        try {
            String response = future.get();
            return parseResponse(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while requesting data from IPGeolocation");
        }

        throw new RuntimeException("Error while requesting data from IPGeolocation");
    }

    private String buildUrl(String lat, String lon) {
        return IP_GEOLOCATION_ASTRONOMY_URL + "?apiKey=" + API_KEY + "&long=" + lon + "&lat=" + lat;
    }

    private SunData parseResponse(String response) {
        try {
            JSONObject jObject = new JSONObject(response);
            Double sunAltitude = jObject.getDouble("sun_altitude");
            Double sunAzimuth = jObject.getDouble("sun_azimuth");
            Double sunDistance = jObject.getDouble("sun_distance");
            return new SunData(sunAltitude, sunAzimuth, sunDistance);
        } catch (JSONException e) {
            throw new RuntimeException("Error while parsing response from IPGeolocation");
        }
    }
}
