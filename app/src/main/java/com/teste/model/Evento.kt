package com.teste.model

data class Evento(
    val date: Long,
    val description: String,
    val image: String,
    val longitude: Double,
    val latitude: Double,
    val price: String,
    val title: String,
    val id: Int
)