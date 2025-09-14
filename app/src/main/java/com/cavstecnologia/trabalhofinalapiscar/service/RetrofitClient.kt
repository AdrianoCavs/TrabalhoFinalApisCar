package com.cavstecnologia.trabalhofinalapiscar.service

import com.cavstecnologia.trabalhofinalapiscar.database.DatabaseBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL_AVD = "http://10.0.2.2:3000/";
    private const val BASE_URL_LAN = "http://192.168.0.249:3000/";

    private val instance: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL_AVD).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY };

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(GeoLocationInterceptor(DatabaseBuilder.getInstance().userLocationDao()))
        .addInterceptor(loggingInterceptor)
        .build();

    val apiService: ApiService = instance.create(ApiService::class.java);
}