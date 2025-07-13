package com.gibran.artistsapp.data.response

import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.model.Image
import com.gibran.artistsapp.domain.model.Member
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDetailResponse(
    val id: Long,
    val name: String,
    val profile: String?,
    val images: List<ImageResponse>?,
    val members: List<MemberResponse>?
) {
    fun toDomain() = ArtistDetail(
        id = id,
        name = name,
        profile = profile,
        images = images?.map { it.toDomain() },
        members = members?.map { it.toDomain() }
    )
}

@JsonClass(generateAdapter = true)
data class MemberResponse(
    val id: Long,
    val name: String
) {
    fun toDomain() = Member(id, name)
}

@JsonClass(generateAdapter = true)
data class ImageResponse(
    @Json(name = "uri") val uri: String
) {
    fun toDomain() = Image(uri)
}