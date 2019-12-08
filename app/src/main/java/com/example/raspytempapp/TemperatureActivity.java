package com.example.raspytempapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.example.raspytempapp.SettingsActivity.SETTINGS_FILE_NAME;

public class TemperatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
    }

    public void getTemperatureSettings(View view) {
        final TextView textView = findViewById(R.id.temperature);

        PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile());

        String url = "http://" + propertyHelper.getHost() + ":8000/api/get/";

        StringRequest stringRequest = buildRequest(textView, url, propertyHelper);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private StringRequest buildRequest(final TextView textView, String url, final PropertyHelper p) {
        return new StringRequest(Request.Method.GET, url, buildResponseListener(textView), buildErrorListener(textView)) {
            @Override
            public Map<String, String> getHeaders() {
                return MainActivity.buildSecurityHeader(p);
            }
        };
    }

    private Response.ErrorListener buildErrorListener(TextView textView) {
        return MainActivity.buildErrorListener(textView);
    }

    private Response.Listener<String> buildResponseListener(final TextView textView) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonObject = new JSONArray(response);
                    textView.setText(jsonObject.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText(e.getMessage());
                }

            }
        };
    }


    private FileInputStream getFile() {
        try {
            return openFileInput(SETTINGS_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
