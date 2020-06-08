package com.smartimagefinder.repository.api;

import androidx.annotation.NonNull;

import com.smartimagefinder.utils.AppUtils;
import com.smartimagefinder.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkInterceptor implements Interceptor {

    private static String API_KEY;

    static {
        System.loadLibrary("keys");
    }

    private native String getAPIKey();

    NetworkInterceptor() {
        //get the API_KEY by calling native JNI code
        API_KEY = getAPIKey();
    }

    @Override
    public @NonNull
    Response intercept(@NonNull Chain chain) throws IOException {
        if (!AppUtils.isInternetConnected()) {
            //before sending request first check for network connection. if no internet connection, throw error
            throw new IOException(Constants.ERROR_NO_NET);
        }
        //add API_KEY in header
        Request request = chain.request().newBuilder().addHeader("Ocp-Apim-Subscription-Key", API_KEY).build();
        return chain.proceed(request);
    }
}
