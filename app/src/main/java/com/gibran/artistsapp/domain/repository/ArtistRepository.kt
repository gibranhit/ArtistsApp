package com.gibran.artistsapp.domain.repository

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Artist
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {

    fun searchArtists(query: String): Flow<PagingData<Artist>>
}