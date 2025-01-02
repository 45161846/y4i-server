package com.example.wrappers

import kotlinx.serialization.Serializable

@Serializable
sealed class RequestResult {
    @Serializable
    data object Success: RequestResult()

    @Serializable
    data class Error(
        val message: String
    ): RequestResult()

    sealed class Authentication: RequestResult(){
        @Serializable
        data object Granted: Authentication()

        @Serializable
        data class Denied(
            val message: String
        ): Authentication()

        @Serializable
        data object NonRegistered: Authentication()
    }

}