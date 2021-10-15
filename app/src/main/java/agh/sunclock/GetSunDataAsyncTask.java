package agh.sunclock;

import android.os.AsyncTask;

import client.IPGeolocationClient;
import model.SunData;

public class GetSunDataAsyncTask extends AsyncTask<Double,String, SunData> {
    private final MainActivity mainActivity;

    public GetSunDataAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected SunData doInBackground(Double... params) {

        // Method runs on a separate thread, make all the network calls you need
        IPGeolocationClient client = new IPGeolocationClient(mainActivity);

        return client.getSunInfo(params[0], params[1]);
    }


    @Override
    protected void onPostExecute(SunData result) {
        mainActivity.setSunData(result);
    }
}