package com.gibran.artistsapp.domain.model

data class Artist(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val resourceUrl: String,
    val type: String
)