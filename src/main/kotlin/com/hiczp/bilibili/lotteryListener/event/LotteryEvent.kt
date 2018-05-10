package com.hiczp.bilibili.lotteryListener.event

import com.hiczp.bilibili.api.live.socket.LiveClient
import com.hiczp.bilibili.api.live.socket.entity.*
import org.springframework.context.ApplicationEvent

abstract class LotteryEvent(source: LiveClient,
                            val dataEntity: DataEntity,
                            val eventType: EventType) : ApplicationEvent(source) {
    fun getSource0(): LiveClient = source as LiveClient
}

class DanMuMsgEvent(source: LiveClient, danMuMsgEntity: DanMuMsgEntity)
    : LotteryEvent(source, danMuMsgEntity, EventType.DANMU_MSG_EVENT)

class SmallTVEvent(source: LiveClient, sysMsgEntity: SysMsgEntity)
    : LotteryEvent(source, sysMsgEntity, EventType.SMALL_TV_EVENT)

class GlobalSpecialGiftEvent(source: LiveClient, sysGiftEntity: SysGiftEntity)
    : LotteryEvent(source, sysGiftEntity, EventType.GLOBAL_SPECIAL_GIFT_EVENT)

class ActivityGiftEvent(source: LiveClient, sysGiftEntity: SysGiftEntity)
    : LotteryEvent(source, sysGiftEntity, EventType.ACTIVITY_GIFT_EVENT)

class SpecialGiftStartEvent(source: LiveClient, specialGiftEntity: SpecialGiftEntity)
    : LotteryEvent(source, specialGiftEntity, EventType.SPECIAL_GIFT_START_EVENT)

class SpecialGiftEndEvent(source: LiveClient, specialGiftEntity: SpecialGiftEntity)
    : LotteryEvent(source, specialGiftEntity, EventType.SPECIAL_GIFT_END_EVENT)

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
