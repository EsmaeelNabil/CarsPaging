package com.esmaeel.softask.data.remote;

import com.esmaeel.softask.data.Models.DataModel;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface WebService {

    @GET("users")
    Single<DataModel> loadChanges(@Query("id") String id);

}
