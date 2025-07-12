package com.gibran.artistsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.usecase.SearchArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ArtistSearchPagingViewModel @Inject constructor(
    private val searchArtistsUseCase: SearchArtistsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val artistsPagingFlow = _searchQuery
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            searchArtistsUseCase(query)
        }
        .cachedIn(viewModelScope)

    val emptyStateFlow = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty<Artist>())
            } else {
                flowOf(null)
            }
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}