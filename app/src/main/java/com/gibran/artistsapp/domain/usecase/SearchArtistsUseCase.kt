package com.gibran.artistsapp.domain.usecase

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.model.Resource
import com.gibran.artistsapp.domain.model.SearchResult
import com.gibran.artistsapp.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArtistsUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Artist>> {
        return repository.searchArtists(query)
    }
}