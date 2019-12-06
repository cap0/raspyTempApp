package com.example.raspytempapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class SettingsActivity extends AppCompatActivity {

    public static final String SETTINGS_FILE_NAME = "settings.property";
    public static final String HOST = "HOST";
    public static final String USER = "USER";
    public static final String PASSWORD = "PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PropertyHelper propertyHelper = new PropertyHelper();
        propertyHelper.load(getFile());

        final TextView hostTxt = findViewById(R.id.host);
        hostTxt.setText(propertyHelper.getHost());

        final TextView userTxt = findViewById(R.id.user);
        userTxt.setText(propertyHelper.getUser());

        final TextView pwdTxt = findViewById(R.id.password);
        pwdTxt.setText(propertyHelper.getPassword());
    }

    private FileInputStream getFile() {
        try {
            return openFileInput(SETTINGS_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveSettings(View view) {
        final TextView hostTxt = findViewById(R.id.host);
        String host = hostTxt.getText().toString();
        final TextView userTxt = findViewById(R.id.user);
        String user = userTxt.getText().toString();
        final TextView pswTxt = findViewById(R.id.password);
        String psw = pswTxt.getText().toString();

        Properties p = new Properties();
        p.setProperty(HOST, host);
        p.setProperty(USER, user);
        p.setProperty(PASSWORD, psw);

        try (FileOutputStream fileOutputStream = openFileOutput(SETTINGS_FILE_NAME, Context.MODE_PRIVATE)){
            p.store(fileOutputStream, "yo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
    }
}
