package com.example.aipodcast.domain.model

data class TranscriptSegment(
    val episodeId: String,
    val startMs: Long,
    val endMs: Long,
    val text: String
)