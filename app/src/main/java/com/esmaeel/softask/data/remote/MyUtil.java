package com.esmaeel.softask.data.remote;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

class MyUtil {
    public static String getLocalLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale.getLanguage();
    }
}
