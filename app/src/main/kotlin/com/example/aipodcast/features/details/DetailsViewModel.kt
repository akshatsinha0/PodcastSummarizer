package com.example.aipodcast.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.domain.usecase.ExportChapters
import com.example.aipodcast.domain.usecase.GetEpisodeDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getEpisodeDetail: GetEpisodeDetail,
    private val exportChapters: ExportChapters
) : ViewModel() {
    
    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()
    
    private val _events = MutableStateFlow<DetailsEvent?>(null)
    val events: StateFlow<DetailsEvent?> = _events.asStateFlow()
    
    fun loadEpisode(episodeId: String) {
        viewModelScope.launch {
            getEpisodeDetail(episodeId).collect { detail ->
                _state.value = _state.value.copy(
                    episode = detail.episode,
                    chapters = detail.chapters,
                    transcript = detail.transcript
                )
            }
        }
    }
    
    fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            DetailsIntent.PlayEpisode -> playEpisode()
            is DetailsIntent.ExportData -> exportData(intent.uri, intent.format)
            DetailsIntent.ClearError -> clearError()
        }
    }
    
    fun clearEvent() {
        _events.value = null
    }
    
    private fun playEpisode() {
        _state.value.episode?.let { episode ->
            _events.value = DetailsEvent.NavigateToPlayer(episode.id)
        }
    }
    
    private fun exportData(uri: android.net.Uri, format: ExportChapters.ExportFormat) {
        viewModelScope.launch {
            _state.value.episode?.let { episode ->
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                when (val result = exportChapters(episode.id, uri, format)) {
                    is Either.Left -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.value.message
                        )
                    }
                    is Either.Right -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _events.value = DetailsEvent.ExportSuccess
                    }
                }
            }
        }
    }
    
    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

sealed class DetailsEvent {
    data class NavigateToPlayer(val episodeId: String) : DetailsEvent()
    object ExportSuccess : DetailsEvent()
}