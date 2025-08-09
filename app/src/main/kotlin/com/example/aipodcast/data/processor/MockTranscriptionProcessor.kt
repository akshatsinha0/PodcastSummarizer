package com.example.aipodcast.data.processor

import android.content.Context
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.model.TranscriptSegment
import com.example.aipodcast.domain.port.TranscriptionProcessor
import kotlinx.coroutines.delay
class MockTranscriptionProcessor : TranscriptionProcessor {
    override suspend fun transcribe(
        context: Context,
        episodeId: String,
        audioSource: String
    ): Either<AppError, List<TranscriptSegment>> {
        return try {
            delay(2000)
            
            val mockSegments = listOf(
                TranscriptSegment(episodeId, 0, 5000, "Welcome to this podcast episode."),
                TranscriptSegment(episodeId, 5000, 12000, "Today we're going to discuss artificial intelligence and its impact on society."),
                TranscriptSegment(episodeId, 12000, 20000, "AI has been transforming various industries from healthcare to finance."),
                TranscriptSegment(episodeId, 20000, 28000, "Let's start by looking at machine learning fundamentals."),
                TranscriptSegment(episodeId, 28000, 35000, "Machine learning algorithms can learn patterns from data without explicit programming."),
                TranscriptSegment(episodeId, 35000, 42000, "There are three main types: supervised, unsupervised, and reinforcement learning."),
                TranscriptSegment(episodeId, 42000, 50000, "Now let's move on to deep learning and neural networks."),
                TranscriptSegment(episodeId, 50000, 58000, "Deep learning uses artificial neural networks with multiple layers."),
                TranscriptSegment(episodeId, 58000, 65000, "These networks can process complex patterns in images, text, and audio."),
                TranscriptSegment(episodeId, 65000, 72000, "Finally, let's discuss the ethical implications of AI technology."),
                TranscriptSegment(episodeId, 72000, 80000, "We need to consider bias, privacy, and the impact on employment."),
                TranscriptSegment(episodeId, 80000, 85000, "Thank you for listening to today's episode.")
            )
            
            Either.Right(mockSegments)
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Transcription failed: ${e.message}"))
        }
    }
}