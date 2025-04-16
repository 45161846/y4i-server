package com.example.wrappers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class RequestResult {

    @Serializable
    data object Success: RequestResult()

    @Serializable
    data class Error(
        val message: String
    ): RequestResult()

    @Serializable
    sealed class Authentication: RequestResult(){

        @Serializable
        @SerialName("granted")
        data object Granted: Authentication()

        @Serializable
        @SerialName("denied")
        data class Denied(
            val message: String
        ): Authentication()

        @Serializable
        @SerialName("non-registered")
        data object NonRegistered: Authentication()
    }

    @Serializable
    sealed class CreateAccount: RequestResult(){

        @Serializable
        @SerialName("created")
        data class Created(
            val token: String
        ): CreateAccount()

        @Serializable
        @SerialName("error")
        data class Error(
            val errorMessage: String
        ): CreateAccount()
    }

    @Serializable
    sealed class PlaylistPage{

        @Serializable
        @SerialName("playlist-page-data")
        data class Data(
            val playlists: List<RemotePlaylist>
        ) : PlaylistPage()

        @Serializable
        @SerialName("noting-more")
        data object NotingMore : PlaylistPage()

        @Serializable
        @SerialName("error")
        data class Error(val message: String) : PlaylistPage()

    }

    @Serializable
    sealed class RemoteTasks{

        @Serializable
        @SerialName("remote-tasks")
        data class Tasks(
            val data: List<PreviewTask>
        ): RemoteTasks()

        data object Error: RemoteTasks()

    }

    @Serializable
    sealed class TaskDownload {

        @Serializable
        data class Data(
            val text: String
        ) : TaskDownload()

        data object Error : TaskDownload()
    }
}