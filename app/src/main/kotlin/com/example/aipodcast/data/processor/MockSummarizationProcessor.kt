package com.example.aipodcast.data.processor

import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.TranscriptSegment
import com.example.aipodcast.domain.port.SummarizationProcessor
import kotlinx.coroutines.delay
class MockSummarizationProcessor : SummarizationProcessor {
    override suspend fun summarizeOverall(segments: List<TranscriptSegment>): Either<AppError, String> {
        return try {
            delay(1500)
            
            val summary = "This podcast episode provides a comprehensive overview of artificial intelligence and its societal impact. The discussion covers machine learning fundamentals, including supervised, unsupervised, and reinforcement learning approaches. The episode then explores deep learning and neural networks, explaining how these technologies process complex patterns in various data types. Finally, the conversation addresses important ethical considerations around AI, including bias, privacy concerns, and employment implications."
            
            Either.Right(summary)
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Summarization failed: ${e.message}"))
        }
    }
    
    override suspend fun generateChapters(segments: List<TranscriptSegment>): Either<AppError, List<Chapter>> {
        return try {
            delay(1000)
            
            val chapters = listOf(
                Chapter(
                    episodeId = segments.firstOrNull()?.episodeId ?: "",
                    startMs = 0,
                    endMs = 20000,
                    title = "Introduction to AI",
                    synopsis = "Welcome and overview of artificial intelligence's impact on society"
                ),
                Chapter(
                    episodeId = segments.firstOrNull()?.episodeId ?: "",
                    startMs = 20000,
                    endMs = 42000,
                    title = "Machine Learning Fundamentals",
                    synopsis = "Exploring the basics of machine learning and its three main types"
                ),
                Chapter(
                    episodeId = segments.firstOrNull()?.episodeId ?: "",
                    startMs = 42000,
                    endMs = 65000,
                    title = "Deep Learning and Neural Networks",
                    synopsis = "Understanding deep learning architectures and their applications"
                ),
                Chapter(
                    episodeId = segments.firstOrNull()?.episodeId ?: "",
                    startMs = 65000,
                    endMs = 85000,
                    title = "AI Ethics and Conclusion",
                    synopsis = "Discussing ethical implications and wrapping up the episode"
                )
            )
            
            Either.Right(chapters)
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Chapter generation failed: ${e.message}"))
        }
    }
}