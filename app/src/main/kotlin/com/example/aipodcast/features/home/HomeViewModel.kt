package com.example.aipodcast.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.domain.usecase.CreateEpisodeFromLocal
import com.example.aipodcast.domain.usecase.CreateEpisodeFromUrl
import com.example.aipodcast.domain.usecase.GetEpisodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getEpisodes: GetEpisodes,
    private val createEpisodeFromLocal: CreateEpisodeFromLocal,
    private val createEpisodeFromUrl: CreateEpisodeFromUrl
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    private val _events = MutableStateFlow<HomeEvent?>(null)
    val events: StateFlow<HomeEvent?> = _events.asStateFlow()
    
    init {
        loadEpisodes()
    }
    
    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.CreateEpisodeFromLocal -> createFromLocal(intent.uri)
            is HomeIntent.CreateEpisodeFromUrl -> createFromUrl(intent.url)
            is HomeIntent.SelectEpisode -> selectEpisode(intent.episodeId)
            HomeIntent.ClearError -> clearError()
        }
    }
    
    fun clearEvent() {
        _events.value = null
    }
    
    private fun loadEpisodes() {
        viewModelScope.launch {
            getEpisodes().collect { episodes ->
                _state.value = _state.value.copy(episodes = episodes)
            }
        }
    }
    
    private fun createFromLocal(uri: android.net.Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = createEpisodeFromLocal(uri)) {
                is Either.Left -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.value.message
                    )
                }
                is Either.Right -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _events.value = HomeEvent.NavigateToProcessing
                }
            }
        }
    }
    
    private fun createFromUrl(url: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = createEpisodeFromUrl(url)) {
                is Either.Left -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.value.message
                    )
                }
                is Either.Right -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _events.value = HomeEvent.NavigateToProcessing
                }
            }
        }
    }
    
    private fun selectEpisode(episodeId: String) {
        _events.value = HomeEvent.NavigateToDetails(episodeId)
    }
    
    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

sealed class HomeEvent {
    data class NavigateToDetails(val episodeId: String) : HomeEvent()
    object NavigateToProcessing : HomeEvent()
}