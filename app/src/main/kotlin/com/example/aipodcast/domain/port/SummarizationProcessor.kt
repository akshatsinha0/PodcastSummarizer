package com.example.aipodcast.domain.port

import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.TranscriptSegment

interface SummarizationProcessor {
    suspend fun summarizeOverall(segments: List<TranscriptSegment>): Either<AppError, String>
    suspend fun generateChapters(segments: List<TranscriptSegment>): Either<AppError, List<Chapter>>
}