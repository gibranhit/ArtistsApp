package com.gibran.artistsapp.domain.model

data class Pagination(
    val perPage: Int,
    val items: Int,
    val page: Int,
    val pages: Int,
    val hasNext: Boolean
)
