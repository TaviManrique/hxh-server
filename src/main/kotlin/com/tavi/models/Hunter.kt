package com.tavi.models

import kotlinx.serialization.Serializable

@Serializable
data class Hunter(
    val id: Int,
    val name: String,
    val image: String,
    val about: String,
    val rating: Double,
    val power: Int,
    val month: String,
    val day: String,
    val family: List<String>,
    val abilities: List<String>,
    val nenTypes: List<String>
)
