package com.example.wrappers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class Credentials{

    @Serializable
    @SerialName("valid")
    data class Valid(
        val login: String,
        val email: String,
        val password: String
    ): Credentials()

    @Serializable
    @SerialName("empty")
    data object Empty: Credentials()
}