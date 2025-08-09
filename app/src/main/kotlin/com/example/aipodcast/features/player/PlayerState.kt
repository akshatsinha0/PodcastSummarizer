package com.example.aipodcast.features.player

import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.Episode

data class PlayerState(
    val episode: Episode? = null,
    val chapters: List<Chapter> = emptyList(),
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val currentChapter: Chapter? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)