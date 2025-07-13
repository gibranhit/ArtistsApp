package com.gibran.artistsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.model.SortOption
import com.gibran.artistsapp.domain.usecase.GetArtistReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DiscographyIntent {
    data class Load(val artistId: Long) : DiscographyIntent
    data class ApplyFilter(val artistId: Long, val filter: DiscographyFilter) : DiscographyIntent
}

@HiltViewModel
class DiscographyViewModel @Inject constructor(
    private val getArtistReleases: GetArtistReleasesUseCase
) : ViewModel() {

    private val _artistId = MutableStateFlow<Long?>(null)
    private val _currentFilter = MutableStateFlow(DiscographyFilter())

    val currentFilterState: StateFlow<DiscographyFilter> = _currentFilter

    @OptIn(ExperimentalCoroutinesApi::class)
    val releases: Flow<PagingData<Release>> = combine(
        _artistId,
        _currentFilter
    ) { artistId, filter ->
        Pair(artistId, filter)
    }.flatMapLatest { (artistId, filter) ->
        if (artistId != null) {
            getArtistReleases(artistId, filter)
                .cachedIn(viewModelScope)
        } else {
            flowOf(PagingData.empty())
        }
    }

    fun onIntent(intent: DiscographyIntent) {
        when (intent) {
            is DiscographyIntent.Load -> {
                _artistId.value = intent.artistId
            }

            is DiscographyIntent.ApplyFilter -> {
                _currentFilter.value = intent.filter
                // No need to set artistId again, combine will trigger automatically
            }
        }
    }
}