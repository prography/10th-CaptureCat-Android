package com.prography.network.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawRequest(
    @SerialName("reason")
    val reason: String
)