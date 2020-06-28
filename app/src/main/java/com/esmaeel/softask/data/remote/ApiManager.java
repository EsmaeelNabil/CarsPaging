package com.esmaeel.softask.data.remote;

import android.app.Activity;
import android.content.Context;

import androidx.viewbinding.BuildConfig;

import com.esmaeel.softask.R;
import com.esmaeel.softask.Utils.Constants;
import com.esmaeel.softask.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private WebService apiService;
    private Gson gson;
    private Retrofit retrofit;
    private OkHttpClient okHttpAuthClient, okHttpLocalClient;


    public WebService getApiService(){
        return apiService;
    }

    /*
     * return a retrofit instance with Auth interceptor added to okhttp
     * */
    public ApiManager(Context authContext) {
        apiService = getRetrofit(getHttpAuthClient(authContext)).create(WebService.class);
    }


    /*
     * return a retrofit instance with only Langugae interceptor added to okhttp
     * */
    public ApiManager(Activity localContextActivity) {
        apiService = getRetrofit(getHttpLocalClient(localContextActivity)).create(WebService.class);
    }


    HttpLoggingInterceptor getLoggingInterceptor() {
        if (BuildConfig.DEBUG) {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
        }
    }


    public Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setLenient().create();
        }
        return gson;
    }


    Interceptor getAuthInterceptor(Context context) {
        return chain -> {
            Request original = chain.request();
            // will send "" empty string if token is null.
            return chain.proceed(chain.request().newBuilder()
                    .header("timezone", TimeZone.getDefault().getID())
                    .header("locale", Utils.getLocalLanguage(context))
/*                    .header("Authorization", PrefUtils.getUserToken(context))*/
                    .method(original.method(), original.body())
                    .build());

        };
    }

    Interceptor getLocalInterceptor(Context context) {
        return chain -> {
            Request original = chain.request();
            return chain.proceed(chain.request().newBuilder()
                    .header("timezone", TimeZone.getDefault().getID())
                    .header("locale", Utils.getLocalLanguage(context))
                    .method(original.method(), original.body())
                    .build());

        };
    }


    Interceptor getCustomErrorInterceptor(Context context) {
        return chain -> {
            // forward the request
            Request request = chain.request();
            // get the response and check it for response code
            Response response = chain.proceed(request);
            switch (response.code()) {
                case 200:
                    if (isFakeResponse(response))
                        return getCustomResponse(request, context.getString(R.string.html_error), 402);
                    else return response;
                case 429:
                    /* return getCustomResponse(request, context.getString(R.string.html_error), 402);*/
                case 404:
                case 503:
                case 500:
                    // open ServerError Activity
                    /*MyUtil.showServerErrorDialog(context);*/
                    break;
                case 401:
                    // send user to login page
                    /*MyUtil.loginAgain(context);*/
                    break;
            }
            return response;

        };
    }

    /*
     * @param request
     * @param errorMessage custom message
     * @param statusCode custom status code
     * @return custom response with
     */
    static Response getCustomResponse(Request request, String errorMessage, Integer statusCode) {
        ResponseBody message =
                ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{\n" + "" +
                                "    \"status\": false,\n" +
                                "    \"msg\": [\n" + errorMessage + "\n" +
                                "    ]\n" +
                                "}");

        return new Response.Builder().request(request)
                .protocol(Protocol.HTTP_1_1)
                .message(errorMessage)
                .body(message)
                .sentRequestAtMillis(-1L)
                .receivedResponseAtMillis(System.currentTimeMillis())
                .code(statusCode)
                .build();
    }

    /**
     * @param response response maybe redirected in case of TeData subscriptions finished
     * @return true or false if the content type is html
     */
    private boolean isFakeResponse(Response response) {
        String header = response.headers().get("Content-Type");
        return header == null || (header.contains("html"));
    }


    OkHttpClient getHttpAuthClient(Context context) {
        if (okHttpAuthClient == null) {
            okHttpAuthClient = new OkHttpClient.Builder()
                    .followRedirects(false)
                    .addInterceptor(getLoggingInterceptor())
                    .addInterceptor(getAuthInterceptor(context))
                    .addInterceptor(getCustomErrorInterceptor(context))
                    .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpAuthClient;
    }


    OkHttpClient getHttpLocalClient(Activity activity) {
        if (okHttpLocalClient == null) {
            okHttpLocalClient = new OkHttpClient.Builder()
                    .followRedirects(false)
                    .addInterceptor(getLoggingInterceptor())
                    .addInterceptor(getLocalInterceptor(activity.getApplicationContext()))
                    .addInterceptor(getCustomErrorInterceptor(activity.getApplicationContext()))
                    .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                    .build();

        }
        return okHttpLocalClient;
    }

    public Retrofit getRetrofit(OkHttpClient client) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.DEBUG ? Constants.DEVELOPMENT_BASE_URL : Constants.PRODUCTIONS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}

