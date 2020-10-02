package com.example.raspytempapp.ui.set_points;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetPointsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SetPointsViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}