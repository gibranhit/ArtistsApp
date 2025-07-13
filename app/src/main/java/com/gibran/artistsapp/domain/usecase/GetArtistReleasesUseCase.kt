package com.gibran.artistsapp.domain.usecase

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.repository.ArtistRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetArtistReleasesUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    operator fun invoke(
        artistId: Long,
        filter: DiscographyFilter = DiscographyFilter()
    ): Flow<PagingData<Release>> = repository.getArtistReleases(artistId, filter)
}