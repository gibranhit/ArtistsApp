package com.gibran.artistsapp.domain.model

data class ArtistDetail(
    val id: Long,
    val name: String,
    val profile: String?,
    val images: List<Image>?,
    val members: List<Member>?
)

data class Member(
    val id: Long,
    val name: String
)

data class Image(
    val uri: String
)
 
