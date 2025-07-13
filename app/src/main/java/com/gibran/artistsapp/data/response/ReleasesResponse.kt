package com.gibran.artistsapp.data.response

import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.ReleasesResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReleasesResponse(
    @Json(name = "pagination") val pagination: PaginationResponse,
    @Json(name = "releases") val releases: List<ReleaseResponse>
)

@JsonClass(generateAdapter = true)
data class ReleaseResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "year") val year: Int?,
    @Json(name = "type") val type: String?,
    @Json(name = "role") val role: String?,
    @Json(name = "thumb") val thumb: String?,
    @Json(name = "label") val label: String?,
    @Json(name = "genre") val genre: String?,
    @Json(name = "format") val format: String?
) {
    fun toDomain() = Release(
        id = id,
        title = title,
        year = year,
        type = type ?: "Unknown",
        role = role ?: "Main",
        imageUrl = thumb,
        label = label,
        genre = genre,
        format = format
    )
}