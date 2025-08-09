package com.example.aipodcast.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.core.util.FilePickers
import com.example.aipodcast.core.util.Hashing
import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.port.EpisodeRepository
import com.example.aipodcast.domain.port.ProcessingRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CreateEpisodeFromLocal(
    private val context: Context,
    private val episodeRepository: EpisodeRepository,
    private val processingRepository: ProcessingRepository
) {
    suspend operator fun invoke(uri: Uri): Either<AppError, String> {
        return try {
            val fileName = FilePickers.getFileName(context, uri) ?: "Unknown Audio"
            val sourcePath = uri.toString()
            val episodeId = Hashing.generateEpisodeId(sourcePath, "local")
            
            val duration = getDuration(uri)
            
            val episode = Episode(
                id = episodeId,
                title = fileName.substringBeforeLast('.'),
                sourceType = "local",
                sourcePath = sourcePath,
                durationMs = duration,
                createdAt = System.currentTimeMillis(),
                processedAt = null,
                summary = null
            )
            
            episodeRepository.insertEpisode(episode)
            processingRepository.scheduleProcessing(episodeId, sourcePath, "local")
            
            Either.Right(episodeId)
        } catch (e: Exception) {
            Either.Left(AppError.FileError("Failed to create episode from local file: ${e.message}"))
        }
    }
    
    private suspend fun getDuration(uri: Uri): Long = suspendCancellableCoroutine { continuation ->
        val player = ExoPlayer.Builder(context).build()
        
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    val duration = player.duration
                    player.release()
                    continuation.resume(if (duration > 0) duration else 300000L)
                }
            }
        })
        
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(MediaMetadata.Builder().build())
            .build()
        
        player.setMediaItem(mediaItem)
        player.prepare()
        
        continuation.invokeOnCancellation {
            player.release()
        }
    }
}