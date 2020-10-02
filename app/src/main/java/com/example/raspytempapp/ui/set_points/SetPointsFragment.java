package com.example.raspytempapp.ui.set_points;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.raspytempapp.MainActivity;
import com.example.raspytempapp.PropertyHelper;
import com.example.raspytempapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import static com.example.raspytempapp.SettingsActivity.SETTINGS_FILE_NAME;

public class SetPointsFragment extends Fragment {

    private SetPointsViewModel setPointsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setPointsViewModel =
                ViewModelProviders.of(this).get(SetPointsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_set_points, container, false);
        final TextView textView = root.findViewById(R.id.text_set_points);
        setPointsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        getTemperatureSettings(root);
        return root;
    }

    private void getTemperatureSettings(View view) {
        final TextView textView = view.findViewById(R.id.text_set_points);

        PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile(view));

        String url = "http://" + propertyHelper.getHost() + ":8000/api/get/";

        StringRequest stringRequest = buildRequest(textView, url, propertyHelper);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
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
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject jo = (JSONObject) jsonObject.get(i);
                        String from = jo.getString("from");
                        String to = jo.getString("to");
                        String value = jo.getString("value");
                        String s = from + " - " + to + "---" +value;
                        textView.append(s + "\n");
                    }
                    textView.setMovementMethod(new ScrollingMovementMethod());
                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText(e.getMessage());
                }

            }
        };
    }


    private FileInputStream getFile(View root) {
        try {
            return root.getContext().openFileInput(SETTINGS_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}