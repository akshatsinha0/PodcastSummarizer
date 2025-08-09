package com.example.aipodcast.features.processing

data class ProcessingState(
    val message: String = "Processing episodes in background...",
    val isProcessing: Boolean = true
)