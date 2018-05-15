package com.hiczp.bilibili.lotteryListener.event

import com.hiczp.bilibili.api.live.socket.LiveClient
import com.hiczp.bilibili.api.live.socket.entity.DataEntity
import com.hiczp.bilibili.api.live.socket.event.ReceivePackageEvent
import org.springframework.context.ApplicationEvent

class LotteryEvent<T : DataEntity>(source: LiveClient,
                                   val eventType: EventType,
                                   val dataEntity: T) : ApplicationEvent(source) {
    constructor(eventType: EventType, receivePackageEvent: ReceivePackageEvent<T>) :
            this(
                    receivePackageEvent.source0,
                    eventType,
                    receivePackageEvent.entity
            )

    fun getSource0(): LiveClient = source as LiveClient
}

/**
 * 使用 Enum 是为了方便数据库存储
 */
enum class EventType {
    /**
     * DanMuMsg 用于测试时调试程序逻辑
     */
    DANMU_MSG_EVENT,
    /**
     * 小电视
     */
    SMALL_TV_EVENT,
    /**
     * 超过 20 倍的节奏风暴
     */
    GLOBAL_SPECIAL_GIFT_EVENT,
    /**
     * 活动礼物
     */
    ACTIVITY_GIFT_EVENT,
    /**
     * 节奏风暴开始(房间内通告的低于 20 倍的节奏风暴, 下同)
     */
    SPECIAL_GIFT_START_EVENT,
    /**
     * 节奏风暴结束
     */
    SPECIAL_GIFT_END_EVENT
}
