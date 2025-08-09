package com.example.aipodcast.core.util

import java.security.MessageDigest

object Hashing {
    fun generateEpisodeId(sourcePath: String, sourceType: String): String {
        val input = "$sourcePath:$sourceType:${System.currentTimeMillis()}"
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(16)
    }
}