package com.esmaeel.softask.data.remote;

import com.esmaeel.softask.data.Models.CarsResponseModel;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WebService {

    @Headers("Content-Type: application/json")
    @GET("cars")
    Single<CarsResponseModel> getCars(@Query("page") Integer pageNumber);

}
