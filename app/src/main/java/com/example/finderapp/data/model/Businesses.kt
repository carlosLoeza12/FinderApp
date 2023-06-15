package com.example.finderapp.data.model

data class BusinessesList(val businesses: List<Business>? = emptyList())

data class Business(
    val id: String? = "",
    val alias: String? = "",
    val name: String? = "",
    val image_url: String? ="",
    val url: String? = "",
    val review_count: String? = "",
    val is_closed: Boolean? = false,
    val categories: List<Categories>? = emptyList(),
    val rating: Float? = 0.0f,
    val coordinates: Coordinates,
    val location: Location,
    val phone: String? = "",
    val display_phone: String? = "",
    val distance: Double? = 0.0,
    val photos: List<String>? = emptyList()
)

data class Categories(
    val alias: String? = "",
    val title: String? = ""
)

data class Coordinates(
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
)

data class Location(
    val address1: String? = "",
    val address2: String? = "",
    val address3: String? = "",
    val city: String? = "",
    val zip_code: String? = "",
    val country: String? = "",
    val state: String? = "",
    val display_address: List<String>? = emptyList()
)