package com.example.aipodcast.features.home

import android.net.Uri

sealed class HomeIntent {
    data class CreateEpisodeFromLocal(val uri: Uri) : HomeIntent()
    data class CreateEpisodeFromUrl(val url: String) : HomeIntent()
    data class SelectEpisode(val episodeId: String) : HomeIntent()
    object ClearError : HomeIntent()
}