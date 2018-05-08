package com.hiczp.bilibili.lotteryListener.listener

import com.google.common.eventbus.Subscribe
import com.hiczp.bilibili.api.live.socket.event.DanMuMsgPackageEvent
import com.hiczp.bilibili.api.live.socket.event.SysMsgPackageEvent
import com.hiczp.bilibili.lotteryListener.event.DanMuMsgEvent
import com.hiczp.bilibili.lotteryListener.event.SmallTVEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

class PrimaryRoomListener(private val applicationContext: ApplicationContext) {
    @Subscribe
    fun onSysMsg(sysMsgPackageEvent: SysMsgPackageEvent) {
        val sysMsgEntity = sysMsgPackageEvent.entity
        //确认其是小电视消息
        if (sysMsgEntity.tvId != null) {
            logger.info("Received smallTV package, " +
                    "tvId ${sysMsgEntity.tvId}, roomId ${sysMsgEntity.roomId}, realRoomId ${sysMsgEntity.realRoomId}")
            applicationContext.publishEvent(SmallTVEvent(sysMsgPackageEvent.source0, sysMsgEntity))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PrimaryRoomListener::class.java)
    }
}

/**
 * 测试用
 */
class PrimaryRoomTestListener(private val applicationContext: ApplicationContext) {
    @Subscribe
    fun onDanMuMsg(danMuMsgPackageEvent: DanMuMsgPackageEvent) {
        val danMuMsgEntity = danMuMsgPackageEvent.entity
        logger.info("Received danMuMsg package, username '${danMuMsgEntity.username}', content '${danMuMsgEntity.message}'")
        applicationContext.publishEvent(
                DanMuMsgEvent(danMuMsgPackageEvent.source0, danMuMsgPackageEvent.entity)
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PrimaryRoomTestListener::class.java)
    }
}
