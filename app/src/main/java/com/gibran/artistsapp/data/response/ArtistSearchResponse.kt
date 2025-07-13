package com.gibran.artistsapp.data.response

import com.gibran.artistsapp.domain.model.Artist
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistSearchResponse(
    @Json(name = "results")
    val results: List<ArtistResponse>,
    @Json(name = "pagination")
    val pagination: PaginationResponse
)

@JsonClass(generateAdapter = true)
data class ArtistResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "title")
    val title: String,
    @Json(name = "thumb")
    val thumb: String?,
    @Json(name = "cover_image")
    val coverImage: String?,
    @Json(name = "resource_url")
    val resourceUrl: String,
    @Json(name = "uri")
    val uri: String,
    @Json(name = "type")
    val type: String
) {
    fun toDomain(): Artist {
        return Artist(
            id = id,
            name = title,
            imageUrl = thumb ?: coverImage,
            resourceUrl = resourceUrl,
            type = type
        )
    }
}

@JsonClass(generateAdapter = true)
data class PaginationResponse(
    @Json(name = "per_page")
    val perPage: Int,
    @Json(name = "items")
    val items: Int,
    @Json(name = "page")
    val page: Int,
    @Json(name = "pages")
    val pages: Int,
    @Json(name = "urls")
    val urls: PaginationUrlsResponse?
)

@JsonClass(generateAdapter = true)
data class PaginationUrlsResponse(
    @Json(name = "last")
    val last: String?,
    @Json(name = "next")
    val next: String?
)