package com.hiczp.bilibili.lotteryListener.model

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class UserCreationModel(
        @field:NotEmpty
        @field:Size(max = 64)
        val username: String,

        @field:NotEmpty
        @field:Size(max = 64)
        val password: String
)
