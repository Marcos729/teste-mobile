package com.teste.controller.service

import com.teste.model.Checkin
import com.teste.model.Evento
import retrofit2.Call
import retrofit2.http.*

interface EventoEndpoint {

    @GET("events")
    fun getEventos() : Call<List<Evento>>

    @GET("events/{idEvento}")
    fun getEvento(@Path("idEvento") id : Int?) : Call<Evento>

    @Headers("Content-Type: application/json")
    @POST("checkin")
    fun getCheckinEvento(@Body userData: Checkin): Call<Checkin>
}