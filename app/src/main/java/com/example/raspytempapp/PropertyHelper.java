package com.example.raspytempapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyHelper {
    public static final String HOST = "HOST";
    public static final String USER = "USER";
    public static final String PASSWORD = "PASSWORD";

    private Properties p;

    public void load(FileInputStream inStream) {
        p = new Properties();
        if (inStream==null) {
            return;
        }

        try {
            p.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getHost(){
        return p.getProperty(HOST, "Host");
    }

    public String getUser(){
        return p.getProperty(USER, "User");
    }

    public String getPassword(){
        return p.getProperty(PASSWORD, "Password");
    }
}
