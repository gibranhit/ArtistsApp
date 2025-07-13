package com.gibran.artistsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.usecase.GetArtistDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ArtistDetailIntent {
    data class Load(val id: Long) : ArtistDetailIntent
    data class Retry(val id: Long) : ArtistDetailIntent
}

sealed interface ArtistDetailEvent {
    object Loading : ArtistDetailEvent
    data class Success(val detail: ArtistDetail) : ArtistDetailEvent
    data class Error(val message: String) : ArtistDetailEvent
}

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val getArtistDetail: GetArtistDetailUseCase
) : ViewModel() {

    private val _event = MutableStateFlow<ArtistDetailEvent>(ArtistDetailEvent.Loading)
    val event: StateFlow<ArtistDetailEvent> = _event

    fun onIntent(intent: ArtistDetailIntent) {
        when (intent) {
            is ArtistDetailIntent.Load -> fetchDetail(intent.id)
            is ArtistDetailIntent.Retry -> fetchDetail(intent.id)
        }
    }

    private fun fetchDetail(id: Long) {
        viewModelScope.launch {
            _event.value = ArtistDetailEvent.Loading
            getArtistDetail(id)
                .onSuccess { detail ->
                    _event.value = ArtistDetailEvent.Success(detail)
                }
                .onFailure { throwable ->
                    _event.value = ArtistDetailEvent.Error(throwable.message ?: "Error desconocido")
                }
        }
    }
}
