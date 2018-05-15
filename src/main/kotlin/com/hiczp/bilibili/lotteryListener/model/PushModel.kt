package com.hiczp.bilibili.lotteryListener.model

import com.hiczp.bilibili.api.live.socket.entity.DataEntity
import com.hiczp.bilibili.lotteryListener.event.EventType
import com.hiczp.bilibili.lotteryListener.event.LotteryEvent

data class PushModel<T : DataEntity>(
        val roomId: Long,
        val realRoomId: Long,
        val eventType: EventType,
        val payload: T
) {
    constructor(lotteryEvent: LotteryEvent<T>) :
            this(
                    lotteryEvent.getSource0().showRoomIdOrRoomId,
                    lotteryEvent.getSource0().roomIdOrShowRoomId,
                    lotteryEvent.eventType,
                    lotteryEvent.dataEntity
            )
}
