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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

sealed interface ArtistSearchIntent {
    data class Search(val query: String) : ArtistSearchIntent
    object ClearSearch : ArtistSearchIntent
}

@HiltViewModel
class ArtistSearchViewModel @Inject constructor(
    private val searchArtistsUseCase: SearchArtistsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(ExperimentalCoroutinesApi::class)
    val artistsPagingFlow: Flow<PagingData<Artist>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isNotBlank()){
                searchArtistsUseCase(query)
                    .cachedIn(viewModelScope)
            } else{
                flowOf(PagingData.empty())
            }
        }

    val isSearchEmpty: StateFlow<Boolean> = _searchQuery
        .map { it.isBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun onIntent(intent: ArtistSearchIntent) {
        when (intent) {
            is ArtistSearchIntent.Search -> {
                _searchQuery.value = intent.query
            }
            ArtistSearchIntent.ClearSearch -> {
                _searchQuery.value = ""
            }
        }
    }
}