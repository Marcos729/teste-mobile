package com.teste.controller.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {

    companion object {

        fun getRetrofitInstance() : Retrofit {
            return Retrofit.Builder()
                .baseUrl("http://5f5a8f24d44d640016169133.mockapi.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}