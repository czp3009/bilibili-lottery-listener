package com.hiczp.bilibili.lotteryListener.hook.model

import com.hiczp.bilibili.lotteryListener.event.EventType
import com.hiczp.bilibili.lotteryListener.hook.dao.Hook
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
