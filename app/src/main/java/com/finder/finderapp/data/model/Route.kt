package com.finder.finderapp.data.model

data class Route(val features: List<Feature>? = emptyList())

data class Feature(val geometry: Geometry? = null, val properties: Properties? = null)
data class Geometry(val coordinates: List<List<Double>>? = emptyList())

data class Properties(val summary: Summary? = null)
data class Summary(
    val distance: Double? = 0.0,
    val duration: Double? = 0.0
)
