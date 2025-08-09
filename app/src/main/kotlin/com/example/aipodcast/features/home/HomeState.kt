package com.example.aipodcast.features.home

import com.example.aipodcast.domain.model.Episode

data class HomeState(
    val episodes: List<Episode> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)