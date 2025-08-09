package com.example.aipodcast

import android.content.Context
import androidx.work.WorkManager
import com.example.aipodcast.core.networking.HttpClientProvider
import com.example.aipodcast.data.api.GeminiApiService
import com.example.aipodcast.data.processor.GeminiSummarizationProcessor
import com.example.aipodcast.data.processor.MockTranscriptionProcessor
import com.example.aipodcast.data.repo.InMemoryEpisodeRepository
import com.example.aipodcast.data.repo.ProcessingRepositoryImpl
import com.example.aipodcast.domain.port.EpisodeRepository
import com.example.aipodcast.domain.port.ProcessingRepository
import com.example.aipodcast.domain.port.SummarizationProcessor
import com.example.aipodcast.domain.port.TranscriptionProcessor
import com.example.aipodcast.domain.usecase.CreateEpisodeFromLocal
import com.example.aipodcast.domain.usecase.CreateEpisodeFromUrl
import com.example.aipodcast.domain.usecase.ExportChapters
import com.example.aipodcast.domain.usecase.GetEpisodeDetail
import com.example.aipodcast.domain.usecase.GetEpisodes
import com.example.aipodcast.playback.PlayerController

class AppContainer(private val context: Context) {
    
    private val workManager by lazy { WorkManager.getInstance(context) }
    private val okHttpClient by lazy { HttpClientProvider.createOkHttpClient() }
    
    private val geminiRetrofit by lazy {
        HttpClientProvider.createRetrofit("https://generativelanguage.googleapis.com/", okHttpClient)
    }
    
    private val geminiApiService by lazy {
        geminiRetrofit.create(GeminiApiService::class.java)
    }
    
    private val transcriptionProcessor: TranscriptionProcessor by lazy { MockTranscriptionProcessor() }
    private val summarizationProcessor: SummarizationProcessor by lazy { 
        GeminiSummarizationProcessor(geminiApiService) 
    }
    
    val episodeRepository: EpisodeRepository by lazy { 
        InMemoryEpisodeRepository() 
    }
    
    val processingRepository: ProcessingRepository by lazy {
        ProcessingRepositoryImpl(context, episodeRepository, transcriptionProcessor, summarizationProcessor, workManager)
    }
    
    val playerController: PlayerController by lazy { PlayerController(context) }
    
    val getEpisodes: GetEpisodes by lazy { GetEpisodes(episodeRepository) }
    val getEpisodeDetail: GetEpisodeDetail by lazy { GetEpisodeDetail(episodeRepository) }
    val createEpisodeFromLocal: CreateEpisodeFromLocal by lazy { 
        CreateEpisodeFromLocal(context, episodeRepository, processingRepository) 
    }
    val createEpisodeFromUrl: CreateEpisodeFromUrl by lazy { 
        CreateEpisodeFromUrl(episodeRepository, processingRepository) 
    }
    val exportChapters: ExportChapters by lazy { ExportChapters(context, episodeRepository) }
}