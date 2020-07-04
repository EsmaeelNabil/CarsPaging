package com.esmaeel.softask.di

import android.content.Context
import com.esmaeel.softask.BuildConfig
import com.esmaeel.softask.Utils.Constants
import com.esmaeel.softask.data.remote.WebService
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object LastNetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthInterceptor

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalInterceptor

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthOkHttp

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalOkHttp


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalService

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthService


    @Provides
    @Singleton
    @AuthInterceptor
    fun ProvideAuthInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            for (header in original.headers) {
                Logger.t("Auth-HEADERS--->").i(header.toString())
            }
            chain.proceed(
                chain.request().newBuilder()
                    .header("timezone", TimeZone.getDefault().id)
                    .header("locale", "MyUtil.getLocalLanguage(context)")
                    .header("Authorization", "PrefUtils.getUserToken(context)")
                    .method(original.method, original.body)
                    .build()
            )
        }
    }


    @Provides
    @Singleton
    @LocalInterceptor
    fun ProvideLocaleInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            for (header in original.headers) {
                Logger.t("Local-HEADERS--->").i(header.toString())
            }
            chain.proceed(
                chain.request().newBuilder()
                    .header("timezone", TimeZone.getDefault().id)
                    .header("locale", "MyUtil.getLocalLanguage(context)")
                    .method(original.method, original.body)
                    .build()
            )
        }
    }


    @Provides
    fun provideBag(): CompositeDisposable = CompositeDisposable()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) HttpLoggingInterceptor(object :
            HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (message.startsWith("{") || message.startsWith("["))
                    Logger.t("JR").json(message)
                else Logger.t("OK_HTTP_MESSAGE_LOGGER").i(message)
            }

        }).setLevel(
            HttpLoggingInterceptor.Level.BODY
        ) else HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.NONE
        )
    }

    @Provides
    @Singleton
    @LocalOkHttp
    fun provideLocalOkHttpClient(
        logger: HttpLoggingInterceptor,
        @LocalInterceptor localInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(localInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @AuthOkHttp
    fun provideAuthOkHttpClient(
        logger: HttpLoggingInterceptor,
        @AuthInterceptor localInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(localInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @LocalService
    fun provideLocalWebService(
        @LocalOkHttp okHttpClient: OkHttpClient
    ): WebService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(Constants.PRODUCTIONS_BASE_URL)
            .build()
            .create(WebService::class.java)
    }


    @Provides
    @Singleton
    @AuthService
    fun provideAuthWebService(
        @AuthOkHttp okHttpClient: OkHttpClient
    ): WebService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(Constants.PRODUCTIONS_BASE_URL)
            .build()
            .create(WebService::class.java)
    }
}