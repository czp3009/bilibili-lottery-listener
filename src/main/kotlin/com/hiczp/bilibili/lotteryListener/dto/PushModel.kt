package com.hiczp.bilibili.lotteryListener.dto

import com.hiczp.bilibili.api.live.socket.entity.DataEntity
import com.hiczp.bilibili.lotteryListener.event.EventType

data class PushModel(
        val eventType: EventType,
        val payload: DataEntity
)
