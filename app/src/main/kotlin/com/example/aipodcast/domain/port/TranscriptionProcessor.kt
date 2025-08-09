package com.example.aipodcast.domain.port

import android.content.Context
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.model.TranscriptSegment

interface TranscriptionProcessor {
    suspend fun transcribe(
        context: Context,
        episodeId: String,
        audioSource: String
    ): Either<AppError, List<TranscriptSegment>>
}