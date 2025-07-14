package com.gibran.artistsapp.data.response

import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.model.Track
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReleasesResponse(
    @Json(name = "pagination") val pagination: PaginationResponse,
    @Json(name = "releases") val releases: List<ReleaseResponse>
)

@JsonClass(generateAdapter = true)
data class ReleaseResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "title")
    val title: String,
    @Json(name = "year")
    val year: Int?,
    @Json(name = "type")
    val type: String,
    @Json(name = "role")
    val role: String,
    @Json(name = "thumb")
    val thumb: String?
) {
    fun toDomain() = Release(
        id = id,
        title = title,
        year = year,
        type = type,
        role = role,
        thumb = thumb
    )
}

@JsonClass(generateAdapter = true)
data class ReleaseDetailResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "title")
    val title: String,
    @Json(name = "year")
    val year: Int?,
    @Json(name = "released")
    val released: String?,
    @Json(name = "genres")
    val genres: List<String>?,
    @Json(name = "styles")
    val styles: List<String>?,
    @Json(name = "tracklist")
    val tracklist: List<TrackResponse>?,
    @Json(name = "images")
    val images: List<ImageResponse>?,
    @Json(name = "labels")
    val labels: List<LabelResponse>?
) {
    fun toDomain() = ReleaseDetail(
        id = id,
        title = title,
        year = year ?: released?.toIntOrNull(),
        genres = genres ?: emptyList(),
        styles = styles ?: emptyList(),
        tracklist = tracklist?.map { it.toDomain() } ?: emptyList(),
        images = images?.map { it.toDomain() },
    )
}

@JsonClass(generateAdapter = true)
data class TrackResponse(
    @Json(name = "position")
    val position: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "duration")
    val duration: String?
) {
    fun toDomain() = Track(
        position = position,
        title = title,
        duration = duration?.takeIf { it.isNotBlank() }
    )
}

@JsonClass(generateAdapter = true)
data class LabelResponse(
    @Json(name = "name")
    val name: String,
    @Json(name = "catno")
    val catno: String?
)
