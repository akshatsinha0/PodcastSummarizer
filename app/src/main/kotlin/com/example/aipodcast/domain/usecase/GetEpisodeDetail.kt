package com.example.aipodcast.domain.usecase

import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.model.TranscriptSegment
import com.example.aipodcast.domain.port.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
data class EpisodeDetail(
    val episode: Episode?,
    val chapters: List<Chapter>,
    val transcript: List<TranscriptSegment>
)

class GetEpisodeDetail(
    private val episodeRepository: EpisodeRepository
) {
    operator fun invoke(episodeId: String): Flow<EpisodeDetail> {
        return combine(
            episodeRepository.getEpisodeByIdFlow(episodeId),
            episodeRepository.getChaptersByEpisodeId(episodeId),
            episodeRepository.getTranscriptByEpisodeId(episodeId)
        ) { episode, chapters, transcript ->
            EpisodeDetail(episode, chapters, transcript)
        }
    }
}