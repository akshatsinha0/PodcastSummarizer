package com.example.aipodcast.data.repo

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.port.EpisodeRepository
import com.example.aipodcast.domain.port.ProcessingRepository
import com.example.aipodcast.domain.port.SummarizationProcessor
import com.example.aipodcast.domain.port.TranscriptionProcessor
import com.example.aipodcast.work.ProcessEpisodeWorker
class ProcessingRepositoryImpl(
    private val context: Context,
    private val episodeRepository: EpisodeRepository,
    private val transcriptionProcessor: TranscriptionProcessor,
    private val summarizationProcessor: SummarizationProcessor,
    private val workManager: WorkManager
) : ProcessingRepository {
    
    override suspend fun scheduleProcessing(episodeId: String, sourcePath: String, sourceType: String) {
        val inputData = Data.Builder()
            .putString(ProcessEpisodeWorker.KEY_EPISODE_ID, episodeId)
            .putString(ProcessEpisodeWorker.KEY_SOURCE_PATH, sourcePath)
            .putString(ProcessEpisodeWorker.KEY_SOURCE_TYPE, sourceType)
            .build()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<ProcessEpisodeWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag("process_episode_$episodeId")
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    override suspend fun processEpisode(episodeId: String, sourcePath: String): Either<AppError, Unit> {
        return try {
            val transcriptResult = transcriptionProcessor.transcribe(context, episodeId, sourcePath)
            when (transcriptResult) {
                is Either.Left -> return Either.Left(transcriptResult.value)
                is Either.Right -> {
                    val segments = transcriptResult.value
                    episodeRepository.insertTranscriptSegments(segments)
                    
                    val chaptersResult = summarizationProcessor.generateChapters(segments)
                    when (chaptersResult) {
                        is Either.Left -> return Either.Left(chaptersResult.value)
                        is Either.Right -> {
                            episodeRepository.insertChapters(chaptersResult.value)
                        }
                    }
                    
                    val summaryResult = summarizationProcessor.summarizeOverall(segments)
                    when (summaryResult) {
                        is Either.Left -> return Either.Left(summaryResult.value)
                        is Either.Right -> {
                            val episode = episodeRepository.getEpisodeById(episodeId)
                            if (episode != null) {
                                episodeRepository.updateEpisode(
                                    episode.copy(
                                        summary = summaryResult.value,
                                        processedAt = System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                    }
                    
                    Either.Right(Unit)
                }
            }
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Episode processing failed: ${e.message}"))
        }
    }
}