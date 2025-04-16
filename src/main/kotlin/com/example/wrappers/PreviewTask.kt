package com.example.wrappers

import com.example.common.Id
import kotlinx.serialization.Serializable

@Serializable
public final data class PreviewTask(
    val remoteId: Id,
    val previewText: String
)