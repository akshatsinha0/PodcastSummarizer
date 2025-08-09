package com.example.aipodcast.features.details

import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.model.TranscriptSegment

data class DetailsState(
    val episode: Episode? = null,
    val chapters: List<Chapter> = emptyList(),
    val transcript: List<TranscriptSegment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)