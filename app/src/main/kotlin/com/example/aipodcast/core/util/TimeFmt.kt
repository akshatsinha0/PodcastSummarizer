package com.example.aipodcast.core.util

object TimeFmt {
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
    
    fun formatTimestamp(timestampMs: Long): String {
        val totalSeconds = timestampMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val millis = (timestampMs % 1000) / 10
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, millis)
        } else {
            String.format("%02d:%02d.%02d", minutes, seconds, millis)
        }
    }
}