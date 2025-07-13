package com.gibran.artistsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gibran.artistsapp.data.api.DiscogsApiService
import com.gibran.artistsapp.data.paging.ArtistPagingSource
import com.gibran.artistsapp.data.paging.ReleasesPagingSource
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.repository.ArtistRepository
import com.gibran.artistsapp.di.DispatcherProvider
import com.gibran.artistsapp.util.safeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.Result

class ArtistRepositoryImpl @Inject constructor(
    private val apiService: DiscogsApiService,
    private val dispatchers: DispatcherProvider
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

    override suspend fun getArtistDetail(id: Long): Result<ArtistDetail> =
        safeApiCall(dispatchers.io) { apiService.getArtistDetails(id) }
            .map { dto -> dto.toDomain() }


    override suspend fun getReleaseDetail(id: Long): Result<ReleaseDetail> =
        safeApiCall(dispatchers.io) { apiService.getReleaseDetails(id) }
            .map { dto -> dto.toDomain() }

    override fun getArtistReleases(
        artistId: Long,
        filter: DiscographyFilter
    ): Flow<PagingData<Release>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                ReleasesPagingSource(apiService, artistId, filter)
            }
        ).flow
    }
}