package com.esmaeel.softask.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.esmaeel.softask.data.Models.ErrorModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class Utils {
    public static String getLocalLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale.getLanguage();
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        netstate = true;
                        break;
                    }
                }
            }
        }
        return netstate;
    }

    public static boolean isNotEmptyOrNull(Collection coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.size() == 0;
    }


    public static String getRxErrorError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return getErrorString(((HttpException) throwable).response().errorBody());
        } else {
            return throwable.getMessage();
        }
    }

    public static String getErrorString(ResponseBody errorBody) {
        Gson gson = new Gson();
        ErrorModel errorModel = null;
        String errorMsg = "Unknown Error";

        try {
            errorModel = gson.fromJson(errorBody.string(), ErrorModel.class);
            errorMsg = errorModel.getError().getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return errorMsg;
        }

        return errorMsg;
    }


}
