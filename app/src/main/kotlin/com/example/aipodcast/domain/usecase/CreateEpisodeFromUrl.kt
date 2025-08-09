package com.example.aipodcast.domain.usecase

import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.core.util.Hashing
import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.port.EpisodeRepository
import com.example.aipodcast.domain.port.ProcessingRepository
class CreateEpisodeFromUrl(
    private val episodeRepository: EpisodeRepository,
    private val processingRepository: ProcessingRepository
) {
    suspend operator fun invoke(url: String): Either<AppError, String> {
        return try {
            if (!isValidUrl(url)) {
                return Either.Left(AppError.NetworkError("Invalid URL format"))
            }
            
            val episodeId = Hashing.generateEpisodeId(url, "url")
            val title = extractTitleFromUrl(url)
            
            val episode = Episode(
                id = episodeId,
                title = title,
                sourceType = "url",
                sourcePath = url,
                durationMs = 0L,
                createdAt = System.currentTimeMillis(),
                processedAt = null,
                summary = null
            )
            
            episodeRepository.insertEpisode(episode)
            processingRepository.scheduleProcessing(episodeId, url, "url")
            
            Either.Right(episodeId)
        } catch (e: Exception) {
            Either.Left(AppError.NetworkError("Failed to create episode from URL: ${e.message}"))
        }
    }
    
    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }
    
    private fun extractTitleFromUrl(url: String): String {
        return url.substringAfterLast('/').substringBeforeLast('.').ifEmpty { "Remote Audio" }
    }
}