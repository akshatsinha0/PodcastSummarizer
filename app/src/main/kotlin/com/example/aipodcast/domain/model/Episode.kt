package com.example.aipodcast.domain.model

data class Episode(
    val id: String,
    val title: String,
    val sourceType: String,
    val sourcePath: String,
    val durationMs: Long,
    val createdAt: Long,
    val processedAt: Long?,
    val summary: String?
)