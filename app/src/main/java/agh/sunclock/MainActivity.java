package agh.sunclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.ipgeolocation.io/astronomy?apiKey=b11b04e7b7a546c6b01ea20c3d79fb7e&lat=41.3558443&long=-74.00776718841271";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    setLabel(response.substring(0,500));
                }, error -> {
                    setLabel("That didn't work!");
                });
        queue.add(stringRequest);
    }

    private void setLabel(String text) {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener( e-> ((TextView)findViewById(R.id.textView)).setText(text));
    }
}