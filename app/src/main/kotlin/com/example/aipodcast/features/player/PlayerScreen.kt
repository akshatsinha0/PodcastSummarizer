package com.example.aipodcast.features.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aipodcast.core.util.TimeFmt
import com.example.aipodcast.domain.model.Chapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    episodeId: String,
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(episodeId) {
        viewModel.loadEpisode(episodeId)
    }
    
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleIntent(PlayerIntent.ClearError)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            state.episode?.let { episode ->
                PlayerControls(
                    episode = episode,
                    isPlaying = state.isPlaying,
                    currentPosition = state.currentPosition,
                    duration = state.duration,
                    currentChapter = state.currentChapter,
                    onPlayPause = {
                        if (state.isPlaying) {
                            viewModel.handleIntent(PlayerIntent.Pause)
                        } else {
                            viewModel.handleIntent(PlayerIntent.Play)
                        }
                    },
                    onSeek = { position ->
                        viewModel.handleIntent(PlayerIntent.SeekTo(position))
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (state.chapters.isNotEmpty()) {
                    Text(
                        text = "Chapters",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.chapters) { chapter ->
                            ChapterItem(
                                chapter = chapter,
                                isCurrentChapter = chapter == state.currentChapter,
                                onClick = {
                                    viewModel.handleIntent(PlayerIntent.SeekToChapter(chapter))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerControls(
    episode: com.example.aipodcast.domain.model.Episode,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    currentChapter: Chapter?,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = episode.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            currentChapter?.let { chapter ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (duration > 0) {
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { onSeek(it.toLong()) },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = TimeFmt.formatDuration(currentPosition),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = TimeFmt.formatDuration(duration),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: Chapter,
    isCurrentChapter: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chapter.title,
                    style = if (isCurrentChapter) {
                        MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)
                    } else {
                        MaterialTheme.typography.titleSmall
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                chapter.synopsis?.let { synopsis ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = synopsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = TimeFmt.formatTimestamp(chapter.startMs),
                style = MaterialTheme.typography.labelSmall,
                color = if (isCurrentChapter) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}