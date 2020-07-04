package com.esmaeel.softask.ui

import com.esmaeel.softask.data.Models.CarsResponseModel
import com.esmaeel.softask.data.remote.WebService
import com.esmaeel.softask.di.LastNetworkModule
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HiltCarsDataPresenter @Inject constructor(
    @LastNetworkModule.AuthService private val mServiceUser: WebService,
    @LastNetworkModule.LocalService private val mService: WebService
) {

    fun getCarsSingle(
        pageNumber: Int
    ): Single<CarsResponseModel> {
        return mService.getCars(pageNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}