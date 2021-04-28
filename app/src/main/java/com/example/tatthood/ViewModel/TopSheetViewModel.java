package com.example.tatthood.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TopSheetViewModel extends ViewModel {
    private final MutableLiveData<Boolean> sheetState = new MutableLiveData<>();

    public LiveData<Boolean> getSheetStatus() {
        return sheetState;
    }

    public void setSheetState(Boolean state) {sheetState.setValue(state);}
}
