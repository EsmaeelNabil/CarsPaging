package com.esmaeel.softask.ui.main;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.esmaeel.softask.Utils.EndlessRecyclerOnScrollListener;
import com.esmaeel.softask.Utils.Utils;
import com.esmaeel.softask.data.Models.CarsResponseModel;
import com.esmaeel.softask.data.remote.ApiManagerDefault;
import com.esmaeel.softask.data.remote.WebService;
import com.esmaeel.softask.databinding.ActivityMainBinding;

public class CarsActivity extends AppCompatActivity {

    private ActivityMainBinding binder;
    private CarsDataPresenter presenter;
    private CarsViewModel viewModel;
    private CarsAdapter carsAdapter;
    private WebService mService;
    private int pageNumber = 1;
    private Boolean hasMore = false;
    private EndlessRecyclerOnScrollListener pagingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        mService = new ApiManagerDefault(CarsActivity.this).getApiService();
        initData();
        initViews();
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(CarsViewModel.class);
        presenter = new CarsDataPresenter(viewModel);
        viewModel.getIsLoading().observe(this, this::ToggleLoader);
        viewModel.getCarsData().observe(this, this::bindCarsToUi);
        viewModel.getErrorMessage().observe(this, this::ErrorToaster);
    }


    private void initViews() {

        binder.swipeLayout.setOnRefreshListener(() -> {
            getCarsData(pageNumber);
        });

        carsAdapter = new CarsAdapter();
        pagingListener = new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage, int visibleItemPosition) {
                if (hasMore) {
                    pageNumber = currentPage;
                    getCarsData(pageNumber);
                }
            }
        };
        binder.carsRecycler.addOnScrollListener(pagingListener);
        binder.carsRecycler.setHasFixedSize(true);
        binder.carsRecycler.setLayoutManager(new LinearLayoutManager(this));
        binder.carsRecycler.setAdapter(carsAdapter);

        getCarsData(pageNumber);
    }

    private void getCarsData(int pageNumber) {
        if (Utils.isNetworkAvailable(getApplicationContext()))
            presenter.getCars(mService, pageNumber);
    }

    private void ErrorToaster(String message) {
        Toast.makeText(CarsActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    private void bindCarsToUi(CarsResponseModel model) {
        carsAdapter.updateCars(model.getData());
    }


    private void ToggleLoader(Boolean bol) {
        binder.swipeLayout.setRefreshing(bol);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.clear();
    }


}