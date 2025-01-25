package com.example.wrappers

import com.example.common.Id
import kotlinx.serialization.Serializable

@Serializable
data class RemotePlaylist(
    val remoteId: Id,
    val title: String,
    val description: String,
    val rating: Float,
    val capacity: Int,
    val previewTasks: List<String>
)