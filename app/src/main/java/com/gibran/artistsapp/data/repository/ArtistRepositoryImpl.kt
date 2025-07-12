package com.gibran.artistsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibran.artistsapp.data.api.DiscogsApiService
import com.gibran.artistsapp.data.paging.ArtistPagingSource
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val apiService: DiscogsApiService
) : ArtistRepository {

    override fun searchArtists(query: String): Flow<PagingData<Artist>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                ArtistPagingSource(apiService, query)
            }
        ).flow
    }
}