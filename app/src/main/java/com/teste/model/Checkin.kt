package com.teste.model

import com.google.gson.annotations.SerializedName

data class Checkin (

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("eventId")
    val eventId: Int
)