package com.example.aipodcast.domain.port

import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.TranscriptSegment

interface Chapterizer {
    suspend fun chapterize(segments: List<TranscriptSegment>): Either<AppError, List<Chapter>>
}