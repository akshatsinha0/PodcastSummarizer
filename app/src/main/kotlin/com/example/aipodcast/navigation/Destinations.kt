package com.example.aipodcast.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Details(val episodeId: String)

@Serializable
data class Player(val episodeId: String)

@Serializable
object Processing