package com.hiczp.bilibili.lotteryListener.listener

import com.google.common.eventbus.Subscribe
import com.hiczp.bilibili.api.live.socket.event.DanMuMsgPackageEvent
import com.hiczp.bilibili.api.live.socket.event.SysGiftPackageEvent
import com.hiczp.bilibili.api.live.socket.event.SysMsgPackageEvent
import com.hiczp.bilibili.lotteryListener.event.ActivityGiftEvent
import com.hiczp.bilibili.lotteryListener.event.DanMuMsgEvent
import com.hiczp.bilibili.lotteryListener.event.GlobalSpecialGiftEvent
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

    @Subscribe
    fun onSysGift(sysGiftPackageEvent: SysGiftPackageEvent) {
        val sysGiftEntity = sysGiftPackageEvent.entity
        val event = when (sysGiftEntity.giftId) {
            null -> return //不可抽奖的礼物
            SPECIAL_GIFT_ID -> {    //超过 20 倍的节奏风暴
                logger.info("Received globalSpecialGift package, " +
                        "roomId ${sysGiftEntity.roomId}")
                GlobalSpecialGiftEvent(sysGiftPackageEvent.source0, sysGiftEntity)
            }
            else -> {    //活动礼物
                logger.info("Received activityGift package, " +
                        "giftId ${sysGiftEntity.giftId}, msg ${sysGiftEntity.msg}, roomId ${sysGiftEntity.roomId}, realRoomId ${sysGiftEntity.realRoomId}")
                ActivityGiftEvent(sysGiftPackageEvent.source0, sysGiftEntity)
            }
        }
        applicationContext.publishEvent(event)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PrimaryRoomListener::class.java)
        private const val SPECIAL_GIFT_ID = 39L
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
