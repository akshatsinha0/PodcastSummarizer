package com.example.aipodcast.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.aipodcast.R
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.domain.port.ProcessingRepository
import com.example.aipodcast.App

class ProcessEpisodeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    private val processingRepository: ProcessingRepository by lazy {
        (applicationContext as App).appContainer.processingRepository
    }
    
    companion object {
        const val KEY_EPISODE_ID = "episode_id"
        const val KEY_SOURCE_PATH = "source_path"
        const val KEY_SOURCE_TYPE = "source_type"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "processing_channel"
    }
    
    override suspend fun doWork(): Result {
        val episodeId = inputData.getString(KEY_EPISODE_ID) ?: return Result.failure()
        val sourcePath = inputData.getString(KEY_SOURCE_PATH) ?: return Result.failure()
        
        setForeground(createForegroundInfo())
        
        return when (val result = processingRepository.processEpisode(episodeId, sourcePath)) {
            is Either.Left -> {
                Result.retry()
            }
            is Either.Right -> {
                Result.success()
            }
        }
    }
    
    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }
    
    private fun createForegroundInfo(): ForegroundInfo {
        createNotificationChannel()
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Processing Episode")
            .setContentText("Transcribing and analyzing audio...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
        
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Episode Processing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of episode processing"
            }
            
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}