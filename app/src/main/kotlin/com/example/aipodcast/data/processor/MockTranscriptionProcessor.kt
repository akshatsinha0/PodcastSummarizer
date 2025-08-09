package com.example.aipodcast.data.processor

import android.content.Context
import com.example.aipodcast.core.model.Either
import com.example.aipodcast.core.model.AppError
import com.example.aipodcast.domain.model.TranscriptSegment
import com.example.aipodcast.domain.port.TranscriptionProcessor
import kotlinx.coroutines.delay
class MockTranscriptionProcessor : TranscriptionProcessor {
    override suspend fun transcribe(
        context: Context,
        episodeId: String,
        audioSource: String
    ): Either<AppError, List<TranscriptSegment>> {
        return try {
            delay(3000)
            
            val mockSegments = generateRealisticTranscript(episodeId, audioSource)
            Either.Right(mockSegments)
        } catch (e: Exception) {
            Either.Left(AppError.ProcessingError("Transcription failed: ${e.message}"))
        }
    }
    
    private fun generateRealisticTranscript(episodeId: String, audioSource: String): List<TranscriptSegment> {
        val topics = listOf(
            "technology and innovation",
            "climate change and sustainability", 
            "entrepreneurship and business",
            "health and wellness",
            "education and learning",
            "artificial intelligence",
            "space exploration",
            "cryptocurrency and finance"
        )
        
        val selectedTopic = topics.random()
        
        return when {
            selectedTopic.contains("technology") -> generateTechTranscript(episodeId)
            selectedTopic.contains("climate") -> generateClimateTranscript(episodeId)
            selectedTopic.contains("business") -> generateBusinessTranscript(episodeId)
            selectedTopic.contains("health") -> generateHealthTranscript(episodeId)
            selectedTopic.contains("AI") -> generateAITranscript(episodeId)
            else -> generateDefaultTranscript(episodeId)
        }
    }
    
    private fun generateTechTranscript(episodeId: String): List<TranscriptSegment> {
        return listOf(
            TranscriptSegment(episodeId, 0, 8000, "Welcome to Tech Talk, the podcast where we explore the latest innovations shaping our digital future."),
            TranscriptSegment(episodeId, 8000, 18000, "I'm your host, and today we're diving deep into the world of quantum computing and its potential to revolutionize everything from cryptography to drug discovery."),
            TranscriptSegment(episodeId, 18000, 28000, "Quantum computers work fundamentally differently from classical computers. Instead of using bits that are either zero or one, they use quantum bits or qubits."),
            TranscriptSegment(episodeId, 28000, 38000, "These qubits can exist in multiple states simultaneously through a phenomenon called superposition, allowing quantum computers to process vast amounts of information in parallel."),
            TranscriptSegment(episodeId, 38000, 48000, "Companies like IBM, Google, and Microsoft are racing to build practical quantum computers that could solve problems impossible for today's machines."),
            TranscriptSegment(episodeId, 48000, 58000, "The implications are staggering. We could see breakthroughs in materials science, artificial intelligence, and even climate modeling."),
            TranscriptSegment(episodeId, 58000, 68000, "However, quantum computing also poses challenges, particularly for cybersecurity. Current encryption methods could become obsolete overnight."),
            TranscriptSegment(episodeId, 68000, 78000, "That's why researchers are already working on quantum-resistant cryptography to protect our digital infrastructure."),
            TranscriptSegment(episodeId, 78000, 88000, "Looking ahead, we're probably still a decade away from widespread quantum computing, but the foundation is being laid today."),
            TranscriptSegment(episodeId, 88000, 95000, "Thanks for joining us on this quantum journey. Until next time, keep innovating!")
        )
    }
    
    private fun generateClimateTranscript(episodeId: String): List<TranscriptSegment> {
        return listOf(
            TranscriptSegment(episodeId, 0, 10000, "Welcome to Climate Solutions, where we explore innovative approaches to tackling our planet's greatest environmental challenges."),
            TranscriptSegment(episodeId, 10000, 22000, "Today we're discussing renewable energy storage, one of the most critical pieces of the clean energy puzzle. Solar and wind power are great, but what happens when the sun doesn't shine and the wind doesn't blow?"),
            TranscriptSegment(episodeId, 22000, 32000, "Battery technology has advanced dramatically in recent years. Lithium-ion batteries have become cheaper and more efficient, making electric vehicles and grid storage more viable."),
            TranscriptSegment(episodeId, 32000, 42000, "But we're also seeing exciting developments in alternative storage methods like compressed air energy storage, pumped hydro, and even gravity-based systems."),
            TranscriptSegment(episodeId, 42000, 52000, "Tesla's Megapack installations in Australia and California have shown how large-scale battery storage can stabilize power grids and reduce reliance on fossil fuel peaker plants."),
            TranscriptSegment(episodeId, 52000, 62000, "The economics are compelling too. Energy storage costs have dropped by over 80% in the last decade, making renewable energy plus storage competitive with traditional power sources."),
            TranscriptSegment(episodeId, 62000, 72000, "Looking forward, we need continued innovation in storage duration, efficiency, and sustainability of the storage systems themselves."),
            TranscriptSegment(episodeId, 72000, 82000, "The transition to clean energy isn't just about generating renewable power - it's about storing and distributing it effectively."),
            TranscriptSegment(episodeId, 82000, 90000, "That's all for today's episode. Remember, every kilowatt-hour stored is a step toward a sustainable future.")
        )
    }
    
    private fun generateBusinessTranscript(episodeId: String): List<TranscriptSegment> {
        return listOf(
            TranscriptSegment(episodeId, 0, 9000, "Welcome to Startup Stories, the podcast where we dive into the journeys of entrepreneurs who are changing the world one innovation at a time."),
            TranscriptSegment(episodeId, 9000, 19000, "Today we're exploring the rise of direct-to-consumer brands and how they're disrupting traditional retail by building direct relationships with customers."),
            TranscriptSegment(episodeId, 19000, 29000, "The D2C model allows brands to control their entire customer experience, from product development to marketing to fulfillment, cutting out traditional middlemen."),
            TranscriptSegment(episodeId, 29000, 39000, "Companies like Warby Parker, Casper, and Dollar Shave Club pioneered this approach, using digital marketing and data analytics to understand and serve their customers better."),
            TranscriptSegment(episodeId, 39000, 49000, "The key advantages include higher profit margins, better customer data, and the ability to iterate quickly based on direct customer feedback."),
            TranscriptSegment(episodeId, 49000, 59000, "However, D2C brands face challenges too. Customer acquisition costs are rising as digital advertising becomes more competitive and expensive."),
            TranscriptSegment(episodeId, 59000, 69000, "Many successful D2C brands are now expanding into wholesale and retail partnerships, creating an omnichannel approach that combines the best of both worlds."),
            TranscriptSegment(episodeId, 69000, 79000, "The lesson for entrepreneurs is clear: focus on building genuine value for customers, whether that's through better products, superior service, or more convenient experiences."),
            TranscriptSegment(episodeId, 79000, 87000, "Thanks for tuning in to Startup Stories. Keep building, keep innovating, and we'll see you next time.")
        )
    }
    
    private fun generateHealthTranscript(episodeId: String): List<TranscriptSegment> {
        return listOf(
            TranscriptSegment(episodeId, 0, 8000, "Welcome to Wellness Weekly, your guide to living a healthier, more balanced life through science-backed insights and practical tips."),
            TranscriptSegment(episodeId, 8000, 18000, "Today we're talking about the fascinating connection between gut health and mental wellbeing, often called the gut-brain axis."),
            TranscriptSegment(episodeId, 18000, 28000, "Recent research has shown that the trillions of bacteria in our digestive system don't just help us digest food - they also produce neurotransmitters that affect our mood and cognitive function."),
            TranscriptSegment(episodeId, 28000, 38000, "About 90% of serotonin, the neurotransmitter associated with happiness and well-being, is actually produced in the gut, not the brain."),
            TranscriptSegment(episodeId, 38000, 48000, "This explains why people with digestive issues often experience anxiety or depression, and why improving gut health can lead to better mental health outcomes."),
            TranscriptSegment(episodeId, 48000, 58000, "So how can we support our gut health? A diverse diet rich in fiber, fermented foods like yogurt and kimchi, and limiting processed foods and artificial sweeteners."),
            TranscriptSegment(episodeId, 58000, 68000, "Regular exercise, adequate sleep, and stress management also play crucial roles in maintaining a healthy gut microbiome."),
            TranscriptSegment(episodeId, 68000, 78000, "The field of psychobiotics - probiotics that can influence mental health - is rapidly evolving and may offer new treatment options in the future."),
            TranscriptSegment(episodeId, 78000, 85000, "Remember, small changes in your daily routine can have big impacts on both your gut and your mood. Thanks for listening to Wellness Weekly.")
        )
    }
    
    private fun generateAITranscript(episodeId: String): List<TranscriptSegment> {
        return listOf(
            TranscriptSegment(episodeId, 0, 8000, "Welcome to AI Insights, where we explore how artificial intelligence is reshaping industries and society."),
            TranscriptSegment(episodeId, 8000, 18000, "Today we're examining the rapid advancement of large language models and their impact on everything from education to creative industries."),
            TranscriptSegment(episodeId, 18000, 28000, "Models like GPT-4 and Claude have demonstrated remarkable capabilities in understanding context, generating human-like text, and even reasoning through complex problems."),
            TranscriptSegment(episodeId, 28000, 38000, "These AI systems are being integrated into tools for writing, coding, research, and customer service, fundamentally changing how we work and learn."),
            TranscriptSegment(episodeId, 38000, 48000, "However, this rapid progress raises important questions about job displacement, misinformation, and the need for AI literacy across all sectors of society."),
            TranscriptSegment(episodeId, 48000, 58000, "The key is finding the right balance - leveraging AI to augment human capabilities while ensuring we maintain critical thinking and creativity."),
            TranscriptSegment(episodeId, 58000, 68000, "Educational institutions are grappling with how to adapt curricula and assessment methods in an age where AI can write essays and solve complex problems."),
            TranscriptSegment(episodeId, 68000, 78000, "Meanwhile, businesses are discovering that the most successful AI implementations focus on enhancing human decision-making rather than replacing human judgment entirely."),
            TranscriptSegment(episodeId, 78000, 85000, "The future belongs to those who can effectively collaborate with AI while maintaining their uniquely human skills. Thanks for joining us on AI Insights.")
        )
    }
    
    private fun generateDefaultTranscript(episodeId: String): List<TranscriptSegment> {
        return listOf(
            TranscriptSegment(episodeId, 0, 8000, "Welcome to today's podcast episode. I'm excited to share some fascinating insights with you."),
            TranscriptSegment(episodeId, 8000, 18000, "We'll be exploring topics that are shaping our world and discussing practical applications you can use in your daily life."),
            TranscriptSegment(episodeId, 18000, 28000, "The landscape of innovation is constantly evolving, and staying informed helps us make better decisions and adapt to change."),
            TranscriptSegment(episodeId, 28000, 38000, "Throughout history, the most successful individuals and organizations have been those that embrace learning and remain curious about new developments."),
            TranscriptSegment(episodeId, 38000, 48000, "Today's discussion will provide you with actionable insights and a deeper understanding of the forces driving change in our interconnected world."),
            TranscriptSegment(episodeId, 48000, 58000, "We'll examine both the opportunities and challenges that lie ahead, helping you navigate an increasingly complex landscape."),
            TranscriptSegment(episodeId, 58000, 68000, "The key takeaway is that continuous learning and adaptation are essential skills for thriving in the modern world."),
            TranscriptSegment(episodeId, 68000, 78000, "I hope you found today's discussion valuable and that it sparks further exploration of these important topics."),
            TranscriptSegment(episodeId, 78000, 85000, "Thank you for listening, and I look forward to continuing this conversation in future episodes.")
        )
    }
}