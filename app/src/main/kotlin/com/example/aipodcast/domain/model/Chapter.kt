package com.example.aipodcast.domain.model

data class Chapter(
    val episodeId: String,
    val startMs: Long,
    val endMs: Long,
    val title: String,
    val synopsis: String?
)