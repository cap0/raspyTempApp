package com.example.raspytempapp.ui.tools;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.raspytempapp.PropertyHelper;
import com.example.raspytempapp.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.activity_settings, container, false);
      //  final TextView textView = root.findViewById(R.id.text_tools);
      /*  toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/
        setup(root);
        return root;
    }

    private static final String SETTINGS_FILE_NAME = "settings.property";
    private static final String HOST = "HOST";
    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    private void setup(View root) {
        PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile(root));

        final TextView hostTxt = root.findViewById(R.id.host);
        hostTxt.setText(propertyHelper.getHost());

        final TextView userTxt = root.findViewById(R.id.user);
        userTxt.setText(propertyHelper.getUser());

        final TextView pwdTxt = root.findViewById(R.id.password);
        pwdTxt.setText(propertyHelper.getPassword());
    }

    private FileInputStream getFile(View root) {
        try {
            return root.getContext().openFileInput(SETTINGS_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveSettings(View view) {
        final TextView hostTxt = view.findViewById(R.id.host);
        String host = hostTxt.getText().toString();
        final TextView userTxt = view.findViewById(R.id.user);
        String user = userTxt.getText().toString();
        final TextView pswTxt = view.findViewById(R.id.password);
        String psw = pswTxt.getText().toString();

        Properties p = new Properties();
        p.setProperty(HOST, host);
        p.setProperty(USER, user);
        p.setProperty(PASSWORD, psw);

        try (FileOutputStream fileOutputStream = view.getContext().openFileOutput(SETTINGS_FILE_NAME, Context.MODE_PRIVATE)){
            p.store(fileOutputStream, "yo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //finish();
    }
}