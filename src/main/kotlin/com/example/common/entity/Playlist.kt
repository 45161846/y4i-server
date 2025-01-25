package com.example.common.entity

data class Playlist(

    val title: String,
    val description: String,
    val capacity: Int,
    val ratingSum: Int,
    val ratingCount: Int,

)

data class PreviewTasks(
    val playlistId: String,
    val text: String,
)