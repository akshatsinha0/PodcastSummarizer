package com.example.aipodcast.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.core.util.Exporters
import com.example.aipodcast.domain.port.EpisodeRepository
class ExportTranscript(
    private val context: Context,
    private val episodeRepository: EpisodeRepository
) {
    suspend operator fun invoke(episodeId: String, uri: Uri, format: Format): Either<AppError, Unit> {
        return try {
            val transcript = episodeRepository.getTranscriptByEpisodeIdSync(episodeId)
            
            val content = when (format) {
                Format.VTT -> Exporters.generateTranscriptVTT(transcript)
                Format.SRT -> Exporters.generateTranscriptSRT(transcript)
            }
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(AppError.FileError("Transcript export failed: ${e.message}"))
        }
    }
    
    enum class Format {
        VTT, SRT
    }
}