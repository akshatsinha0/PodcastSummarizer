package com.example.aipodcast.core.util

import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.TranscriptSegment
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Exporters {
    fun generateWebVTT(chapters: List<Chapter>): String {
        val builder = StringBuilder("WEBVTT\n\n")
        chapters.forEach { chapter ->
            builder.append("${TimeFmt.formatTimestamp(chapter.startMs)} --> ${TimeFmt.formatTimestamp(chapter.endMs)}\n")
            builder.append("${chapter.title}\n")
            if (chapter.synopsis?.isNotEmpty() == true) {
                builder.append("${chapter.synopsis}\n")
            }
            builder.append("\n")
        }
        return builder.toString()
    }
    
    fun generateSRT(chapters: List<Chapter>): String {
        val builder = StringBuilder()
        chapters.forEachIndexed { index, chapter ->
            builder.append("${index + 1}\n")
            builder.append("${TimeFmt.formatTimestamp(chapter.startMs).replace('.', ',')} --> ${TimeFmt.formatTimestamp(chapter.endMs).replace('.', ',')}\n")
            builder.append("${chapter.title}\n")
            if (chapter.synopsis?.isNotEmpty() == true) {
                builder.append("${chapter.synopsis}\n")
            }
            builder.append("\n")
        }
        return builder.toString()
    }
    
    fun generateTranscriptVTT(segments: List<TranscriptSegment>): String {
        val builder = StringBuilder("WEBVTT\n\n")
        segments.forEach { segment ->
            builder.append("${TimeFmt.formatTimestamp(segment.startMs)} --> ${TimeFmt.formatTimestamp(segment.endMs)}\n")
            builder.append("${segment.text}\n\n")
        }
        return builder.toString()
    }
    
    fun generateTranscriptSRT(segments: List<TranscriptSegment>): String {
        val builder = StringBuilder()
        segments.forEachIndexed { index, segment ->
            builder.append("${index + 1}\n")
            builder.append("${TimeFmt.formatTimestamp(segment.startMs).replace('.', ',')} --> ${TimeFmt.formatTimestamp(segment.endMs).replace('.', ',')}\n")
            builder.append("${segment.text}\n\n")
        }
        return builder.toString()
    }
    
    @Serializable
    data class ExportData(
        val summary: String?,
        val chapters: List<ChapterExport>,
        val transcript: List<TranscriptExport>
    )
    
    @Serializable
    data class ChapterExport(
        val startMs: Long,
        val endMs: Long,
        val title: String,
        val synopsis: String?
    )
    
    @Serializable
    data class TranscriptExport(
        val startMs: Long,
        val endMs: Long,
        val text: String
    )
    
    fun generateJSON(summary: String?, chapters: List<Chapter>, transcript: List<TranscriptSegment>): String {
        val exportData = ExportData(
            summary = summary,
            chapters = chapters.map { ChapterExport(it.startMs, it.endMs, it.title, it.synopsis) },
            transcript = transcript.map { TranscriptExport(it.startMs, it.endMs, it.text) }
        )
        return Json.encodeToString(exportData)
    }
}