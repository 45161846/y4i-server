package com.example.common

import kotlinx.serialization.Serializable


@JvmInline
@Serializable
value class Id(
    val value: Long
)