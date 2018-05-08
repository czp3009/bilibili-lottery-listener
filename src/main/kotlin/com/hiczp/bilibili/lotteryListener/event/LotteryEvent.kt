package com.hiczp.bilibili.lotteryListener.event

import com.hiczp.bilibili.api.live.socket.LiveClient
import com.hiczp.bilibili.api.live.socket.entity.DanMuMsgEntity
import com.hiczp.bilibili.api.live.socket.entity.DataEntity
import com.hiczp.bilibili.api.live.socket.entity.SysMsgEntity
import org.springframework.context.ApplicationEvent

abstract class LotteryEvent(source: LiveClient,
                            val dataEntity: DataEntity,
                            val eventType: EventType) : ApplicationEvent(source)

class DanMuMsgEvent(source: LiveClient, danMuMsgEntity: DanMuMsgEntity) :
        LotteryEvent(source, danMuMsgEntity, EventType.DANMU_MSG_EVENT)

class SmallTVEvent(source: LiveClient, sysMsgEntity: SysMsgEntity) :
        LotteryEvent(source, sysMsgEntity, EventType.SMALL_TV_EVENT)

enum class EventType {
    /**
     * DanMuMsg 用于测试时调试程序逻辑
     */
    DANMU_MSG_EVENT,
    SMALL_TV_EVENT
}
