package com.example.aipodcast.features.details

import android.net.Uri
import com.example.aipodcast.domain.usecase.ExportChapters

sealed class DetailsIntent {
    object PlayEpisode : DetailsIntent()
    data class ExportData(val uri: Uri, val format: ExportChapters.ExportFormat) : DetailsIntent()
    object ClearError : DetailsIntent()
}