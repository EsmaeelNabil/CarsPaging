//package com.esmaeel.softask.di
//
//import android.content.Context
//import com.esmaeel.softask.BuildConfig
//import com.esmaeel.softask.R
//import com.esmaeel.softask.Utils.Constants
//import com.esmaeel.softask.Utils.Utils
//import com.esmaeel.softask.data.remote.WebService
//import com.google.gson.Gson
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
//import dagger.hilt.android.qualifiers.ApplicationContext
//import io.reactivex.disposables.CompositeDisposable
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.Response
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
//import retrofit2.converter.gson.GsonConverterFactory
//import timber.log.Timber
//import java.util.*
//import java.util.concurrent.TimeUnit
//import javax.inject.Qualifier
//import javax.inject.Singleton
//
//@Module
//@InstallIn(ApplicationComponent::class)
//class AppModule {
//
//
//    @Qualifier
//    @Retention(AnnotationRetention.BINARY)
//    annotation class LocalWebService
//
//    @Qualifier
//    @Retention(AnnotationRetention.BINARY)
//    annotation class AuthWebService
//
//
//    @Provides
//    @Singleton
//    @LocalWebService
//    fun LocalWebService(
//        @ApplicationContext context: Context,
//        gson: Gson?
//    ): WebService {
//        return getRetrofit(
//            HttpLocalClient(context),
//            gson
//        ).create(
//            WebService::class.java
//        )
//    }
//
//    @Provides
//    @Singleton
//    @AuthWebService
//    fun AuthWebService(
//        @ApplicationContext context: Context,
//        gson: Gson
//    ): WebService {
//        return getRetrofit(
//            getHttpAuthClient(context),
//            gson
//        ).create(
//            WebService::class.java
//        )
//    }
//
//    @Provides
//    fun CompositeDisposable(): CompositeDisposable = CompositeDisposable()
//
//    @Singleton
//    @Provides
//    fun getGson(): Gson = Gson()
//
//
//    @Singleton
//    @Provides
//    fun loggingInterceptor(): HttpLoggingInterceptor {
//        return if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
//                override fun log(message: String) {
//                    Timber.tag(
//                        "HttpLoggingInterceptor"
//                    ).i(message)
//                }
//            }).setLevel(HttpLoggingInterceptor.Level.BODY)
//        } else {
//            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
//        }
//
//    }
//
//    @Provides
//    @Singleton
//    fun getLocalInterceptor(@ApplicationContext context: Context?): Interceptor {
//        return Interceptor { chain: Interceptor.Chain ->
//            val original = chain.request()
//            chain.proceed(
//                chain.request().newBuilder()
//                    .header("timezone", TimeZone.getDefault().id)
//                    .header("locale", "EN" /*MyUtil.getLocalLanguage(context)*/)
//                    .method(original.method, original.body)
//                    .build()
//            )
//        }
//    }
//
//    private fun isFakeResponse(response: Response): Boolean {
//        val header = response.headers["Content-Type"]
//        return header == null || header.contains("html")
//    }
//
//    @Provides
//    @Singleton
//    fun getCustomErrorInterceptor(@ApplicationContext context: Context): Interceptor {
//        return label@ Interceptor { chain: Interceptor.Chain ->
//            // forward the request
//            val request = chain.request()
//            // get the response and check it for response code
//            val response = chain.proceed(request)
//            when (response.code) {
//                200 -> if (isFakeResponse(response)) return@label Utils.getCustomResponse(
//                    request,
//                    context.getString(R.string.html_error),
//                    402
//                ) else return@label response
//                429, 404, 503, 500 -> {
//                }
//                401 -> {
//                }
//            }
//            response
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun HttpLocalClient(@ApplicationContext context: Context): OkHttpClient {
//        return OkHttpClient.Builder()
//            .followRedirects(false)
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor(getLocalInterceptor(context))
//            .addInterceptor(getCustomErrorInterceptor(context))
//            .readTimeout(
//                Constants.NETWORK_TIMEOUT,
//                TimeUnit.SECONDS
//            )
//            .connectTimeout(
//                Constants.NETWORK_TIMEOUT,
//                TimeUnit.SECONDS
//            )
//            .writeTimeout(
//                Constants.NETWORK_TIMEOUT,
//                TimeUnit.SECONDS
//            )
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun getAuthInterceptor(@ApplicationContext context: Context?): Interceptor {
//        return Interceptor { chain: Interceptor.Chain ->
//            val original = chain.request()
//            chain.proceed(
//                chain.request().newBuilder()
//                    .header("timezone", TimeZone.getDefault().id)
//                    .header("locale", "EN" /*MyUtil.getLocalLanguage(context)*/)
//                    .header("Authorization", "TOKEN" /*PrefUtils.getUserToken(context)*/)
//                    .method(original.method, original.body)
//                    .build()
//            )
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun getHttpAuthClient(@ApplicationContext context: Context): OkHttpClient {
//        return OkHttpClient.Builder()
//            .followRedirects(false)
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor(getAuthInterceptor(context))
//            .addInterceptor(getCustomErrorInterceptor(context))
//            .readTimeout(
//                Constants.NETWORK_TIMEOUT,
//                TimeUnit.SECONDS
//            )
//            .connectTimeout(
//                Constants.NETWORK_TIMEOUT,
//                TimeUnit.SECONDS
//            )
//            .writeTimeout(
//                Constants.NETWORK_TIMEOUT,
//                TimeUnit.SECONDS
//            )
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun getRetrofit(client: OkHttpClient?, gson: Gson?): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(Constants.PRODUCTIONS_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .client(client)
//            .build()
//    }
//}