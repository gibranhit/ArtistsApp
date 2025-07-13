package com.gibran.artistsapp.domain.repository

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.DiscographyFilter
import kotlinx.coroutines.flow.Flow
import kotlin.Result

interface ArtistRepository {

    fun searchArtists(query: String): Flow<PagingData<Artist>>

    suspend fun getArtistDetail(id: Long): Result<ArtistDetail>

    fun getArtistReleases(
        artistId: Long,
        filter: DiscographyFilter = DiscographyFilter()
    ): Flow<PagingData<Release>>
}