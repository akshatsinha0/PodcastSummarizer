package com.example.aipodcast.features.player

import com.example.aipodcast.domain.model.Chapter

sealed class PlayerIntent {
    object Play : PlayerIntent()
    object Pause : PlayerIntent()
    data class SeekTo(val positionMs: Long) : PlayerIntent()
    data class SeekToChapter(val chapter: Chapter) : PlayerIntent()
    object ClearError : PlayerIntent()
}