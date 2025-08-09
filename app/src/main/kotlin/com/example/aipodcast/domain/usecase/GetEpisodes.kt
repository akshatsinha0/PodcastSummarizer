package com.example.aipodcast.domain.usecase

import com.example.aipodcast.domain.model.Episode
import com.example.aipodcast.domain.port.EpisodeRepository
import kotlinx.coroutines.flow.Flow
class GetEpisodes(
    private val episodeRepository: EpisodeRepository
) {
    operator fun invoke(): Flow<List<Episode>> {
        return episodeRepository.getAllEpisodes()
    }
}