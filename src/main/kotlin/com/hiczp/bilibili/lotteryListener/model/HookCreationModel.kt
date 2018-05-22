package com.hiczp.bilibili.lotteryListener.model

import com.hiczp.bilibili.lotteryListener.dao.Hook
import com.hiczp.bilibili.lotteryListener.event.EventType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class HookCreationModel(
        @NotEmpty
        var eventType: EventType,

        @NotEmpty
        @Size(max = 255)
        var url: String
) {
    fun toHook() =
            Hook(
                    eventType = eventType,
                    url = url
            )
}
