package com.example.aipodcast.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.core.util.Exporters
import com.example.aipodcast.domain.port.EpisodeRepository
class ExportChapters(
    private val context: Context,
    private val episodeRepository: EpisodeRepository
) {
    suspend operator fun invoke(episodeId: String, uri: Uri, format: ExportFormat): Either<AppError, Unit> {
        return try {
            val chapters = episodeRepository.getChaptersByEpisodeIdSync(episodeId)
            val transcript = episodeRepository.getTranscriptByEpisodeIdSync(episodeId)
            val episode = episodeRepository.getEpisodeById(episodeId)
            
            val content = when (format) {
                ExportFormat.VTT -> Exporters.generateWebVTT(chapters)
                ExportFormat.SRT -> Exporters.generateSRT(chapters)
                ExportFormat.TRANSCRIPT_VTT -> Exporters.generateTranscriptVTT(transcript)
                ExportFormat.TRANSCRIPT_SRT -> Exporters.generateTranscriptSRT(transcript)
                ExportFormat.JSON -> Exporters.generateJSON(episode?.summary, chapters, transcript)
            }
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(AppError.FileError("Export failed: ${e.message}"))
        }
    }
    
    enum class ExportFormat {
        VTT, SRT, TRANSCRIPT_VTT, TRANSCRIPT_SRT, JSON
    }
}