package com.hiczp.bilibili.lotteryListener.listener

import com.google.common.eventbus.Subscribe
import com.hiczp.bilibili.api.live.socket.event.GuardLotteryStartPackageEvent
import com.hiczp.bilibili.api.live.socket.event.SpecialGiftPackageEvent
import com.hiczp.bilibili.lotteryListener.event.EventType
import com.hiczp.bilibili.lotteryListener.event.publishLotteryEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class NormalRoomListener(private val applicationContext: ApplicationContext) {
    @Subscribe
    fun onSpecialGift(specialGiftPackageEvent: SpecialGiftPackageEvent) {
        val specialGift = specialGiftPackageEvent.entity.data.`$39`
        logger.info("Received specialGift package, " +
                "roomId ${specialGiftPackageEvent.source0.roomIdOrShowRoomId}, id ${specialGift.id}, action '${specialGift.action}'")
        when (specialGift.action) {
            "start" -> EventType.SPECIAL_GIFT_START_EVENT
            "end" -> EventType.SPECIAL_GIFT_END_EVENT
            else -> {
                logger.error("Received SPECIAL_GIFT package with unknown action '${specialGift.action}'")
                return
            }
        }.run {
            applicationContext.publishLotteryEvent(this, specialGiftPackageEvent)
        }
    }

    @Subscribe
    fun onGuardLotteryStart(guardLotteryStartPackageEvent: GuardLotteryStartPackageEvent) {
        logger.info("Received guardLotteryStart package, " +
                "lottery room: ${guardLotteryStartPackageEvent.entity.data.roomId}")
        applicationContext.publishLotteryEvent(EventType.GUARD_LOTTERY_START, guardLotteryStartPackageEvent)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(NormalRoomListener::class.java)
    }
}
