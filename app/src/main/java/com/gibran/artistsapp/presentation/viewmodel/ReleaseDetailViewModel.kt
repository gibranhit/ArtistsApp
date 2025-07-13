package com.gibran.artistsapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.usecase.GetReleaseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReleaseDetailIntent {
    data class Load(val id: Long) : ReleaseDetailIntent
    data class Retry(val id: Long) : ReleaseDetailIntent
}

sealed interface ReleaseDetailEvent {
    object Loading : ReleaseDetailEvent
    data class Success(val detail: ReleaseDetail) : ReleaseDetailEvent
    data class Error(val message: String) : ReleaseDetailEvent
}

@HiltViewModel
class ReleaseDetailViewModel @Inject constructor(
    private val getReleaseDetail: GetReleaseDetailUseCase
) : ViewModel() {

    private val _event = MutableStateFlow<ReleaseDetailEvent>(ReleaseDetailEvent.Loading)
    val event: StateFlow<ReleaseDetailEvent> = _event

    fun onIntent(intent: ReleaseDetailIntent) {
        when (intent) {
            is ReleaseDetailIntent.Load -> fetchDetail(intent.id)
            is ReleaseDetailIntent.Retry -> fetchDetail(intent.id)
        }
    }

    private fun fetchDetail(id: Long) {
        viewModelScope.launch {
            _event.value = ReleaseDetailEvent.Loading
            getReleaseDetail(id)
                .onSuccess { detail ->
                    _event.value = ReleaseDetailEvent.Success(detail)
                }
                .onFailure { throwable ->
                    _event.value =
                        ReleaseDetailEvent.Error(throwable.message ?: "Error desconocido")
                }
        }
    }
}