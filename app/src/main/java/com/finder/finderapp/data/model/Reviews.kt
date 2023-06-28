package com.finder.finderapp.data.model

data class Reviews(
    val reviews: List<Review>? = emptyList(),
)

data class Review(
    val id: String? = "",
    val url: String? = "",
    val text: String?= "",
    val rating: Int? = 0,
    val time_created: String? = "",
    val user: User? = null
)

data class User(
    val id: String? = "",
    val profile_url: String? = "",
    val image_url: String? = "",
    val name: String? = ""
)
