package com.esmaeel.softask.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoadingM = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return isLoadingM;
    }

    public void setIsLoading(Boolean isLoading) {
        this.isLoadingM.setValue(isLoading);
    }
}
