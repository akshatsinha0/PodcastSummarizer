package com.example.aipodcast.features.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipodcast.domain.usecase.GetEpisodeDetail
import com.example.aipodcast.playback.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val getEpisodeDetail: GetEpisodeDetail,
    private val playerController: PlayerController
) : ViewModel() {
    
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()
    
    fun loadEpisode(episodeId: String) {
        viewModelScope.launch {
            combine(
                getEpisodeDetail(episodeId),
                playerController.isPlaying,
                playerController.currentPosition,
                playerController.duration,
                playerController.currentChapter
            ) { detail, isPlaying, position, duration, currentChapter ->
                PlayerState(
                    episode = detail.episode,
                    chapters = detail.chapters,
                    isPlaying = isPlaying,
                    currentPosition = position,
                    duration = duration,
                    currentChapter = currentChapter
                )
            }.collect { newState ->
                _state.value = newState
                
                if (newState.episode != null && _state.value.chapters.isNotEmpty()) {
                    setupPlayer(newState.episode.sourcePath, newState.chapters)
                }
            }
        }
    }
    
    fun handleIntent(intent: PlayerIntent) {
        when (intent) {
            PlayerIntent.Play -> playerController.play()
            PlayerIntent.Pause -> playerController.pause()
            is PlayerIntent.SeekTo -> playerController.seekTo(intent.positionMs)
            is PlayerIntent.SeekToChapter -> playerController.seekToChapter(intent.chapter)
            PlayerIntent.ClearError -> clearError()
        }
    }
    
    private fun setupPlayer(sourcePath: String, chapters: List<com.example.aipodcast.domain.model.Chapter>) {
        try {
            val uri = Uri.parse(sourcePath)
            playerController.setMediaSource(uri)
            playerController.setChapters(chapters)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = "Failed to load audio: ${e.message}")
        }
    }
    
    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}