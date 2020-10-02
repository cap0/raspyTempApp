package com.example.raspytempapp.ui.setTemperature;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.raspytempapp.PropertyHelper;
import com.example.raspytempapp.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.example.raspytempapp.SettingsActivity.SETTINGS_FILE_NAME;

public class TemperatureSetter extends Fragment {

    private TemperatureSetterModel temperatureSetterModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        temperatureSetterModel =
                ViewModelProviders.of(this).get(TemperatureSetterModel.class);
        return inflater.inflate(R.layout.fragment_temperature_setter, container, false);
    }

    public void sendMessage(View view) {
        final TextView inputTxt = view.findViewById(R.id.editText);
        final TextView outputTxt = view.findViewById(R.id.res);
        outputTxt.setText("");
        String setting = inputTxt.getText().toString();

        PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile(view.getContext()));

        String url = "http://"+propertyHelper.getHost()+":8000/api/set/" +setting;

        StringRequest stringRequest = buildRequest(outputTxt, url, propertyHelper);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        queue.add(stringRequest);
    }

    private FileInputStream getFile(Context c) {
        try {
            return c.openFileInput(SETTINGS_FILE_NAME);
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

    public static Map<String, String> buildSecurityHeader(PropertyHelper p) {
        Map<String, String> headers = new HashMap<>();
        String credentials = p.getUser()+":" +p.getPassword();
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", auth);
        return headers;
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