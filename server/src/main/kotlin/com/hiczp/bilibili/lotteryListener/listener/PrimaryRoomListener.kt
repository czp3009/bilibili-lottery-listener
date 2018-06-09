package com.hiczp.bilibili.lotteryListener.listener

import com.google.common.eventbus.Subscribe
import com.hiczp.bilibili.api.live.socket.event.DanMuMsgPackageEvent
import com.hiczp.bilibili.api.live.socket.event.SysGiftPackageEvent
import com.hiczp.bilibili.api.live.socket.event.SysMsgPackageEvent
import com.hiczp.bilibili.lotteryListener.event.EventType
import com.hiczp.bilibili.lotteryListener.event.publishLotteryEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

interface PrimaryRoomListener

@Component
class DefaultPrimaryRoomListener(private val applicationContext: ApplicationContext) : PrimaryRoomListener {
    @Subscribe
    fun onSysMsg(sysMsgPackageEvent: SysMsgPackageEvent) {
        val sysMsgEntity = sysMsgPackageEvent.entity
        //确认其是小电视消息
        sysMsgEntity.tvId
                ?.takeIf {
                    it.isNotEmpty() && (it != "0")
                }
                ?.run {
                    logger.info("Received smallTV package, " +
                            "tvId $this, roomId ${sysMsgEntity.roomId}, realRoomId ${sysMsgEntity.realRoomId}")
                    applicationContext.publishLotteryEvent(EventType.SMALL_TV_EVENT, sysMsgPackageEvent)
                }
    }

    @Subscribe
    fun onSysGift(sysGiftPackageEvent: SysGiftPackageEvent) {
        val sysGiftEntity = sysGiftPackageEvent.entity
        when (sysGiftEntity.giftId) {
            null -> return //不可抽奖的礼物
            SPECIAL_GIFT_ID -> {    //超过 20 倍的节奏风暴
                logger.info("Received globalSpecialGift package, " +
                        "roomId ${sysGiftEntity.roomId}")
                EventType.GLOBAL_SPECIAL_GIFT_EVENT
            }
            else -> {    //活动礼物
                logger.info("Received activityGift package, " +
                        "giftId ${sysGiftEntity.giftId}, msg ${sysGiftEntity.msg}, roomId ${sysGiftEntity.roomId}, realRoomId ${sysGiftEntity.realRoomId}")
                EventType.ACTIVITY_GIFT_EVENT
            }
        }.run {
            applicationContext.publishLotteryEvent(this, sysGiftPackageEvent)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DefaultPrimaryRoomListener::class.java)
        private const val SPECIAL_GIFT_ID = 39L
    }
}

/**
 * 测试用
 */
@Profile("dev")
@Component
class TestPrimaryRoomListener(private val applicationContext: ApplicationContext) : PrimaryRoomListener {
    @Subscribe
    fun onDanMuMsg(danMuMsgPackageEvent: DanMuMsgPackageEvent) {
        val danMuMsgEntity = danMuMsgPackageEvent.entity
        logger.info("Received danMuMsg package, username '${danMuMsgEntity.username}', content '${danMuMsgEntity.message}'")
        applicationContext.publishLotteryEvent(EventType.DANMU_MSG_EVENT, danMuMsgPackageEvent)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestPrimaryRoomListener::class.java)
    }
}
