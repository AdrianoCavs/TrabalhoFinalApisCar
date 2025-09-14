package com.cavstecnologia.trabalhofinalapiscar.model

data class Car(
    val id: String,
    val value: CarValue
);

data class CarValue(
    val id: String,
    val year: String,
    val name: String,
    val licence: String,
    val imageUrl: String,
    val location: CarLocation
);

data class CarLocation(
    val lat: Double,
    val long: Double,
    //val name: String
);