package com.esmaeel.softask.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.esmaeel.softask.data.Models.CarsResponseModel;

class CarsViewModel extends BaseViewModel {

    private MutableLiveData<CarsResponseModel> carsData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public void setCarsData(CarsResponseModel responseModel) {
        carsData.setValue(responseModel);
    }

    public void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    public LiveData<CarsResponseModel> getCarsData() {
        return carsData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
