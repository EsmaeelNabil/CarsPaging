package com.esmaeel.softask.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esmaeel.softask.Utils.Utils
import com.esmaeel.softask.data.Models.CarsResponseModel
import com.esmaeel.softask.ui.main.BaseViewModel
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class HiltCarsViewModel @ViewModelInject constructor(
    private val carsDataPresenter: HiltCarsDataPresenter,
    private val bag: CompositeDisposable
) : BaseViewModel() {


    private val carsData = MutableLiveData<CarsResponseModel>()
    private val errorMessage = MutableLiveData<String>()


    private fun setCarsData(responseModel: CarsResponseModel) {
        carsData.value = responseModel
    }

    private fun setErrorMessage(message: String) {
        errorMessage.value = message
    }

    fun getCars(page: Int) {
        carsDataPresenter.getCarsSingle(page)
            .subscribe(object : SingleObserver<CarsResponseModel> {
                override fun onSubscribe(d: Disposable) {
                    setIsLoading(true)
                    bag.add(d)
                }

                override fun onSuccess(carsResponseModel: CarsResponseModel) {
                    if (carsResponseModel.isSuccessful) {
                        if (Utils.isNotEmptyOrNull(carsResponseModel.data)) {
                            setCarsData(carsResponseModel)
                        }
                    } else {
                        setErrorMessage(carsResponseModel.error.message)
                    }
                    setIsLoading(false)
                }

                override fun onError(e: Throwable) {
                    setIsLoading(false)
                    setErrorMessage(Utils.getRxErrorError(e))
                }
            })
    }

    fun getCarsLiveData(): LiveData<CarsResponseModel> {
        return carsData
    }

    fun getErrorMessageLiveData(): LiveData<String> {
        return errorMessage
    }
}