package com.example.aipodcast.domain.port

import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.model.TranscriptSegment
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getAllEpisodes(): Flow<List<Episode>>
    suspend fun getEpisodeById(episodeId: String): Episode?
    fun getEpisodeByIdFlow(episodeId: String): Flow<Episode?>
    suspend fun insertEpisode(episode: Episode)
    suspend fun updateEpisode(episode: Episode)
    suspend fun deleteEpisode(episodeId: String)
    
    fun getChaptersByEpisodeId(episodeId: String): Flow<List<Chapter>>
    suspend fun getChaptersByEpisodeIdSync(episodeId: String): List<Chapter>
    suspend fun insertChapters(chapters: List<Chapter>)
    
    fun getTranscriptByEpisodeId(episodeId: String): Flow<List<TranscriptSegment>>
    suspend fun getTranscriptByEpisodeIdSync(episodeId: String): List<TranscriptSegment>
    suspend fun insertTranscriptSegments(segments: List<TranscriptSegment>)
}