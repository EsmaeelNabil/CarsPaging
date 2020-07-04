package com.esmaeel.softask

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


@HiltAndroidApp
class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()

        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(5)
            .tag("GLOBAL_LOGGER_")
            .build()

        val logger = object : AndroidLogAdapter(PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(5)
            .tag("GLOBAL_LOGGER_")
            .build()) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        }

        Logger.addLogAdapter(logger)

        Timber.plant(object : DebugTree() {
            override fun log(
                priority: Int,
                tag: String?,
                message: String,
                t: Throwable?
            ) {
                Logger.log(priority, tag, message, t)
            }
        })
    }
}