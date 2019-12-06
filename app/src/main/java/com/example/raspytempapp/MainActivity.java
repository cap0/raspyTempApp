package com.example.raspytempapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.example.raspytempapp.SettingsActivity.SETTINGS_FILE_NAME;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void gotoView(View view) {
        Intent intent = new Intent(this, TemperatureActivity.class);
        startActivity(intent);
    }

    public void sendMessage(View view) {
        final TextView inputTxt = findViewById(R.id.editText);
        final TextView outputTxt = findViewById(R.id.res);
        outputTxt.setText("");
        String setting = inputTxt.getText().toString();

        PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile());

        String url = "http://"+propertyHelper.getHost()+":8000/api/set/" +setting;

        StringRequest stringRequest = buildRequest(outputTxt, url, propertyHelper);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private FileInputStream getFile() {
        try {
            return openFileInput(SETTINGS_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private StringRequest buildRequest(final TextView textView, String url, final PropertyHelper p) {
        return new StringRequest(Request.Method.GET, url, buildResponseListener(textView), buildErrorListener(textView)) {
            @Override
            public Map<String, String> getHeaders() {
                return buildSecurityHeader(p);
            }
        };
    }

    public static Map<String, String> buildSecurityHeader(PropertyHelper p) {
        Map<String, String> headers = new HashMap<>();
        String credentials = p.getUser()+":" +p.getPassword();
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", auth);
        return headers;
    }

    public static Response.ErrorListener buildErrorListener(final TextView textView) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    textView.setText("Timeout");
                } else {
                    textView.setText("Fail");
                }
            }
        };
    }

    private Response.Listener<String> buildResponseListener(final TextView textView) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText("ok");
            }
        };
    }
}
