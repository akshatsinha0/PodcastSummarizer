package com.example.aipodcast.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.aipodcast.domain.model.Chapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerController(
    private val context: Context
) {
    private val _player = ExoPlayer.Builder(context).build()
    val player: ExoPlayer = _player
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _currentChapter = MutableStateFlow<Chapter?>(null)
    val currentChapter: StateFlow<Chapter?> = _currentChapter.asStateFlow()
    
    private var chapters: List<Chapter> = emptyList()
    
    init {
        _player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = _player.duration
                }
            }
        })
        
        startPositionUpdates()
    }
    
    fun setMediaSource(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        _player.setMediaItem(mediaItem)
        _player.prepare()
    }
    
    fun setChapters(newChapters: List<Chapter>) {
        chapters = newChapters
        updateCurrentChapter()
    }
    
    fun play() {
        _player.play()
    }
    
    fun pause() {
        _player.pause()
    }
    
    fun seekTo(positionMs: Long) {
        _player.seekTo(positionMs)
        updateCurrentChapter()
    }
    
    fun seekToChapter(chapter: Chapter) {
        seekTo(chapter.startMs)
    }
    
    fun release() {
        _player.release()
    }
    
    private fun startPositionUpdates() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                if (_player.isPlaying) {
                    _currentPosition.value = _player.currentPosition
                    updateCurrentChapter()
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateRunnable)
    }
    
    private fun updateCurrentChapter() {
        val position = _currentPosition.value
        val chapter = chapters.find { position >= it.startMs && position < it.endMs }
        if (chapter != _currentChapter.value) {
            _currentChapter.value = chapter
        }
    }
}