package com.cavstecnologia.trabalhofinalapiscar.service

import com.cavstecnologia.trabalhofinalapiscar.database.dao.UserLocationDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


//TODO PELO QUE OLHEI NO CÓDIGO ESSE CARA NÃO SERÁ NECESSÁRIO E NÃO EXISTEM HEADERS NO NODE DE CAR
class GeoLocationInterceptor(private val userLocationDao: UserLocationDao): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val userLocationLast = runBlocking { userLocationDao.getLastLocation() }


        val originalRequest: Request = chain.request();
        val newRequest = originalRequest.newBuilder()
            .addHeader("latitude", userLocationLast?.latitude.toString())
            .addHeader("longitude", userLocationLast?.longitude.toString())
            .build();
        return chain.proceed(newRequest);
    }
}