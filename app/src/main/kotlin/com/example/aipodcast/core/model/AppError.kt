package com.example.aipodcast.core.model

sealed class AppError(val message: String) {
    data class NetworkError(val error: String) : AppError(error)
    data class FileError(val error: String) : AppError(error)
    data class ProcessingError(val error: String) : AppError(error)
    data class DatabaseError(val error: String) : AppError(error)
    data class UnknownError(val error: String) : AppError(error)
}