package com.example.aipodcast.data.processor

import com.example.aipodcast.BuildConfig
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.data.api.Content
import com.example.aipodcast.data.api.GeminiApiService
import com.example.aipodcast.data.api.GeminiRequest
import com.example.aipodcast.data.api.Part
import com.example.aipodcast.domain.model.Chapter
import com.example.aipodcast.domain.model.TranscriptSegment
import com.example.aipodcast.domain.port.SummarizationProcessor
import kotlinx.serialization.json.Json

class GeminiSummarizationProcessor(
    private val geminiApi: GeminiApiService
) : SummarizationProcessor {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun summarizeOverall(segments: List<TranscriptSegment>): Either<AppError, String> {
        return try {
            val transcript = segments.joinToString(" ") { it.text }
            
            val prompt = """
                Please provide a comprehensive summary of this podcast episode transcript. 
                Focus on the main topics, key insights, and important takeaways.
                Keep the summary concise but informative (2-3 paragraphs).
                
                Transcript:
                $transcript
            """.trimIndent()
            
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(prompt))
                    )
                )
            )
            
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val summary = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Unable to generate summary"
            
            Either.Right(summary.trim())
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Failed to generate summary: ${e.message}"))
        }
    }
    
    override suspend fun generateChapters(segments: List<TranscriptSegment>): Either<AppError, List<Chapter>> {
        return try {
            val transcript = segments.joinToString("\n") { 
                "[${formatTime(it.startMs)} - ${formatTime(it.endMs)}] ${it.text}" 
            }
            
            val prompt = """
                Analyze this timestamped podcast transcript and create topic-based chapters.
                Return the result as a JSON array with this exact format:
                [
                  {
                    "startMs": 0,
                    "endMs": 120000,
                    "title": "Chapter Title",
                    "synopsis": "Brief description of what's covered in this chapter"
                  }
                ]
                
                Guidelines:
                - Create 3-6 meaningful chapters based on topic changes
                - Each chapter should be at least 30 seconds long
                - Titles should be descriptive and engaging
                - Synopsis should be 1-2 sentences explaining the chapter content
                - Use the exact timestamp format from the transcript
                
                Transcript:
                $transcript
            """.trimIndent()
            
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(prompt))
                    )
                )
            )
            
            val response = geminiApi.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Either.Left(AppError.ProcessingError("No response from Gemini"))
            
            val chapters = parseChaptersFromResponse(responseText, segments.firstOrNull()?.episodeId ?: "")
            Either.Right(chapters)
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Failed to generate chapters: ${e.message}"))
        }
    }
    
    private fun parseChaptersFromResponse(responseText: String, episodeId: String): List<Chapter> {
        return try {
            val jsonStart = responseText.indexOf('[')
            val jsonEnd = responseText.lastIndexOf(']') + 1
            
            if (jsonStart == -1 || jsonEnd <= jsonStart) {
                return createFallbackChapters(episodeId)
            }
            
            val jsonText = responseText.substring(jsonStart, jsonEnd)
            val chaptersData = json.decodeFromString<List<ChapterData>>(jsonText)
            
            chaptersData.map { data ->
                Chapter(
                    episodeId = episodeId,
                    startMs = data.startMs,
                    endMs = data.endMs,
                    title = data.title,
                    synopsis = data.synopsis
                )
            }
        } catch (e: Exception) {
            createFallbackChapters(episodeId)
        }
    }
    
    private fun createFallbackChapters(episodeId: String): List<Chapter> {
        return listOf(
            Chapter(
                episodeId = episodeId,
                startMs = 0,
                endMs = 30000,
                title = "Introduction",
                synopsis = "Opening segment of the episode"
            ),
            Chapter(
                episodeId = episodeId,
                startMs = 30000,
                endMs = 60000,
                title = "Main Discussion",
                synopsis = "Core content and main topics"
            ),
            Chapter(
                episodeId = episodeId,
                startMs = 60000,
                endMs = 90000,
                title = "Conclusion",
                synopsis = "Closing thoughts and wrap-up"
            )
        )
    }
    
    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes}:${remainingSeconds.toString().padStart(2, '0')}"
    }
    
    @kotlinx.serialization.Serializable
    private data class ChapterData(
        val startMs: Long,
        val endMs: Long,
        val title: String,
        val synopsis: String
    )
}