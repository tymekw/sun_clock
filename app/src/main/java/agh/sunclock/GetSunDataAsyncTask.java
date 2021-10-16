package agh.sunclock;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Optional;

import client.IPGeolocationClient;
import model.SunData;

public class GetSunDataAsyncTask extends AsyncTask<Optional<Double>, String, SunData> {
    private final MainActivity mainActivity;

    public GetSunDataAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected SunData doInBackground(Optional<Double>... params) {

        // Method runs on a separate thread, make all the network calls you need
        IPGeolocationClient client = new IPGeolocationClient(mainActivity);
        while (!mainActivity.getLatitude().isPresent() || !mainActivity.getLongitude().isPresent()) {
            System.out.println("Params: " + params[0] + "  " + params[1]);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Got lat&lon going for request");

        return client.getSunInfo(mainActivity.getLatitude().get(), mainActivity.getLongitude().get());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(SunData result) {
        mainActivity.setSunData(result);
        mainActivity.changeToMainView();
    }
}