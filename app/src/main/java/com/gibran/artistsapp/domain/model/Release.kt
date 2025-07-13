package com.gibran.artistsapp.domain.model

data class Release(
    val id: Long,
    val title: String,
    val year: Int?,
    val type: String, // album, single, compilation, etc.
    val role: String, // main, appearance, etc.
    val imageUrl: String?,
    val label: String?,
    val genre: String?,
    val format: String? // vinyl, cd, digital, etc.
)

data class ReleasesResult(
    val releases: List<Release>,
    val pagination: Pagination
)

// Filter data class for discography
data class DiscographyFilter(
    val sortBy: SortOption = SortOption.YEAR_DESC
)

enum class SortOption(val apiSort: String, val apiOrder: String, val displayName: String) {
    YEAR_DESC("year", "desc", "Año (Reciente)"),
    YEAR_ASC("year", "asc", "Año (Antiguo)"),
    TITLE_ASC("title", "asc", "Título (A-Z)"),
    TITLE_DESC("title", "desc", "Título (Z-A)"),
    FORMAT_ASC("format", "asc", "Formato")
}