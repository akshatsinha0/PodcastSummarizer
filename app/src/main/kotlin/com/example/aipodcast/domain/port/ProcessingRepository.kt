package com.example.aipodcast.domain.port

import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError

interface ProcessingRepository {
    suspend fun scheduleProcessing(episodeId: String, sourcePath: String, sourceType: String)
    suspend fun processEpisode(episodeId: String, sourcePath: String): Either<AppError, Unit>
}