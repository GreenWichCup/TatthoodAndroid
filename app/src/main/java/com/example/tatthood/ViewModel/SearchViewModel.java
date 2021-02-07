package com.example.tatthood.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<String> status = new MutableLiveData<>();

    public void selectStatus(String statusString) {
        status.setValue(statusString);
    }

    public LiveData<String> getSelectedStatus() {
        return status;
    }


    }

