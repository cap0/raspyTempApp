package com.example.raspytempapp.ui.home;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.example.raspytempapp.SettingsActivity.SETTINGS_FILE_NAME;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        getActualTemperature(root);
        return root;
    }

    public void getActualTemperature( View root) {
        final TextView room = root.findViewById(R.id.roomTemp2);
        final TextView wort = root.findViewById(R.id.wortTemp2);
        final TextView res = root.findViewById(R.id.res2);

        final PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile(root));

        String url = "http://" + propertyHelper.getHost() + ":8000/api/temperature/";

        StringRequest sr =  new StringRequest(Request.Method.GET, url, handleGetTemperatureResponse(res, room, wort), buildErrorListener(res)) {
            @Override
            public Map<String, String> getHeaders() {
                return buildSecurityHeader(propertyHelper);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(root.getContext());
        queue.add(sr);
    }

    private FileInputStream getFile(View root) {
        try {
            return root.getContext().openFileInput(SETTINGS_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> buildSecurityHeader(PropertyHelper p) {
        Map<String, String> headers = new HashMap<>();
        String credentials = p.getUser()+":" +p.getPassword();
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", auth);
        return headers;
    }

    private Response.Listener<String> handleGetTemperatureResponse(final TextView textView, final TextView room, final TextView wort) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String r = jsonObject.getString("room");
                    String w = jsonObject.getString("wort");

                    room.setText(r);
                    wort.setText(w);

                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText("error");

                }
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
}