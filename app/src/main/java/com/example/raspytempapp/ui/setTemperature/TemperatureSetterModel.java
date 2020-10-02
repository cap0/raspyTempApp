package com.example.raspytempapp.ui.setTemperature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TemperatureSetterModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TemperatureSetterModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}