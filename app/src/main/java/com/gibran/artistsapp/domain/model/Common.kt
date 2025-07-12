package com.gibran.artistsapp.domain.model

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
}

data class Pagination(
    val perPage: Int,
    val items: Int,
    val page: Int,
    val pages: Int,
    val hasNext: Boolean
)

data class SearchResult<T>(
    val items: List<T>,
    val pagination: Pagination
)