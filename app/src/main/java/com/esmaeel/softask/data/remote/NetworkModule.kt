package com.esmaeel.softask.data.remote

import android.content.Context
import com.esmaeel.softask.BuildConfig
import com.esmaeel.softask.Utils.Constants
import com.esmaeel.softask.Utils.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


//@InstallIn(ApplicationComponent::class)
object NetworkModule {

    /*use in both local and token services*/
    open class CustomErrorInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            // get the response and check it for response code
            val response = chain.proceed(request)
            when (response.code) {
                200 -> {
/*
                    return if (isFakeResponse(response)) getCustomResponse(
                        request,
                        context.getString(R.string.html_error),
                        402
                    ) else response
*/
                }
                429 -> {
/*
                    if (MyUtilKotlin.isFakeResponse(response))
                        return MyUtilKotlin.getCustomResponse(
                            request,
                            context.getString(R.string.html_error),
                            402
                        )
                    else
                        return response
*/
                }
                404, 503, 500 -> { /*show Error Page*/
                    // open ServerError Activity
/*                    MyUtil.showServerErrorDialog(context)*/
                }
                401 -> {/* do Auth Thing*/
                    // send user to login page
                    /*                  MyUtil.loginAgain(context)*/
                    return response
                }
            }
            return response
        }
    }

    /*use in token service only*/
    class AuthInterceptor(val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val newReq = chain.request().newBuilder()
                .header("timezone", TimeZone.getDefault().id)
                .header("locale", Utils.getLocalLanguage(context))
//                .header("Authorization", PrefUtils.getUserToken(context))
                .method(original.method, original.body)
                .build()

            return chain.proceed(newReq)
        }
    }

    class LocalInterceptor( val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            return chain.proceed(
                chain.request().newBuilder()
                    .header("timezone", TimeZone.getDefault().id)
                    .header("locale", Utils.getLocalLanguage(context))
                    .method(original.method, original.body)
                    .build()
            )
        }
    }

    fun HttpAuthClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        customErrorInterceptor: CustomErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(false)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(customErrorInterceptor)
            .readTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    fun HttpLocaleClient(
        loggingInterceptor: HttpLoggingInterceptor,
        localInterceptor: LocalInterceptor,
        customErrorInterceptor: CustomErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .followRedirects(false)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(localInterceptor)
            .addInterceptor(customErrorInterceptor)
            .readTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }


    fun LoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }


    fun getGson(): Gson {
        return GsonBuilder().setLenient().create()
    }


    fun getLocalApiService(client: OkHttpClient): WebService {
        return Retrofit.Builder()
            .baseUrl(Constants.PRODUCTIONS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
            .create(WebService::class.java)
    }

    fun getAuthApiService(client: OkHttpClient): WebService {
        return Retrofit.Builder()
            .baseUrl(Constants.PRODUCTIONS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
            .create(WebService::class.java)
    }




}