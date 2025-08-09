package com.example.aipodcast.data.repo

import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.model.TranscriptSegment
import com.example.aipodcast.domain.port.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class InMemoryEpisodeRepository : EpisodeRepository {
    
    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    private val _transcripts = MutableStateFlow<List<TranscriptSegment>>(emptyList())
    
    override fun getAllEpisodes(): Flow<List<Episode>> {
        return _episodes.asStateFlow()
    }
    
    override suspend fun getEpisodeById(episodeId: String): Episode? {
        return _episodes.value.find { it.id == episodeId }
    }
    
    override fun getEpisodeByIdFlow(episodeId: String): Flow<Episode?> {
        return _episodes.map { episodes -> episodes.find { it.id == episodeId } }
    }
    
    override suspend fun insertEpisode(episode: Episode) {
        val currentEpisodes = _episodes.value.toMutableList()
        val existingIndex = currentEpisodes.indexOfFirst { it.id == episode.id }
        if (existingIndex >= 0) {
            currentEpisodes[existingIndex] = episode
        } else {
            currentEpisodes.add(episode)
        }
        _episodes.value = currentEpisodes
    }
    
    override suspend fun updateEpisode(episode: Episode) {
        insertEpisode(episode)
    }
    
    override suspend fun deleteEpisode(episodeId: String) {
        _episodes.value = _episodes.value.filter { it.id != episodeId }
        _chapters.value = _chapters.value.filter { it.episodeId != episodeId }
        _transcripts.value = _transcripts.value.filter { it.episodeId != episodeId }
    }
    
    override fun getChaptersByEpisodeId(episodeId: String): Flow<List<Chapter>> {
        return _chapters.map { chapters -> chapters.filter { it.episodeId == episodeId } }
    }
    
    override suspend fun getChaptersByEpisodeIdSync(episodeId: String): List<Chapter> {
        return _chapters.value.filter { it.episodeId == episodeId }
    }
    
    override suspend fun insertChapters(chapters: List<Chapter>) {
        val currentChapters = _chapters.value.toMutableList()
        chapters.forEach { newChapter ->
            val existingIndex = currentChapters.indexOfFirst { 
                it.episodeId == newChapter.episodeId && it.startMs == newChapter.startMs 
            }
            if (existingIndex >= 0) {
                currentChapters[existingIndex] = newChapter
            } else {
                currentChapters.add(newChapter)
            }
        }
        _chapters.value = currentChapters
    }
    
    override fun getTranscriptByEpisodeId(episodeId: String): Flow<List<TranscriptSegment>> {
        return _transcripts.map { transcripts -> transcripts.filter { it.episodeId == episodeId } }
    }
    
    override suspend fun getTranscriptByEpisodeIdSync(episodeId: String): List<TranscriptSegment> {
        return _transcripts.value.filter { it.episodeId == episodeId }
    }
    
    override suspend fun insertTranscriptSegments(segments: List<TranscriptSegment>) {
        val currentTranscripts = _transcripts.value.toMutableList()
        segments.forEach { newSegment ->
            val existingIndex = currentTranscripts.indexOfFirst { 
                it.episodeId == newSegment.episodeId && it.startMs == newSegment.startMs 
            }
            if (existingIndex >= 0) {
                currentTranscripts[existingIndex] = newSegment
            } else {
                currentTranscripts.add(newSegment)
            }
        }
        _transcripts.value = currentTranscripts
    }
}