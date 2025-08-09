package com.example.aipodcast.core.model

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
    
    fun isLeft(): Boolean = this is Left
    fun isRight(): Boolean = this is Right
    
    fun leftOrNull(): L? = when (this) {
        is Left -> value
        is Right -> null
    }
    
    fun rightOrNull(): R? = when (this) {
        is Left -> null
        is Right -> value
    }
}