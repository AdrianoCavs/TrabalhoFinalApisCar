package com.cavstecnologia.trabalhofinalapiscar.service

import android.os.Build
import com.cavstecnologia.trabalhofinalapiscar.database.DatabaseBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL_AVD = "http://10.0.2.2:3000/";
    private const val BASE_URL_LAN = "http://192.168.0.249:3000/";

    private val instance: Retrofit by lazy {
        if (isEmulator()){
            Retrofit.Builder().baseUrl(BASE_URL_AVD).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
        } else{
            Retrofit.Builder().baseUrl(BASE_URL_LAN).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
        }

    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY };

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(GeoLocationInterceptor(DatabaseBuilder.getInstance().userLocationDao()))
        .addInterceptor(loggingInterceptor)
        .build();

    val apiService: ApiService = instance.create(ApiService::class.java);
}

fun isEmulator(): Boolean {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_google")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("simulator")
}