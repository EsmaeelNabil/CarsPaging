package com.esmaeel.softask.ui.main;

import com.esmaeel.softask.Utils.Utils;
import com.esmaeel.softask.data.Models.CarsResponseModel;
import com.esmaeel.softask.data.remote.WebService;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CarsDataPresenter {
    private CarsViewModel viewModel;
    private CompositeDisposable bag;

    public void clear() {
        if (bag != null)
            bag.clear();
    }

    public CarsDataPresenter(CarsViewModel viewModel) {
        this.viewModel = viewModel;
        bag = new CompositeDisposable();
    }


    public void getCars(WebService mService, int pageNumber) {
        mService.getCars(pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CarsResponseModel>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        viewModel.setIsLoading(true);
                        bag.add(d);
                    }

                    @Override
                    public void onSuccess(CarsResponseModel carsResponseModel) {
                        if (carsResponseModel.isSuccessful()) {
                            if (Utils.isNotEmptyOrNull(carsResponseModel.getData())) {
                                viewModel.setCarsData(carsResponseModel);
                            }
                        }
                        viewModel.setIsLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        viewModel.setIsLoading(false);
                        viewModel.setErrorMessage(Utils.getRxErrorError(e));
                    }
                });
    }
}
