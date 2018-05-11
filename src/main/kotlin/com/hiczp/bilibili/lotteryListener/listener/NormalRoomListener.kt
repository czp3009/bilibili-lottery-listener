package com.hiczp.bilibili.lotteryListener.listener

import com.google.common.eventbus.Subscribe
import com.hiczp.bilibili.api.live.socket.event.SpecialGiftPackageEvent
import com.hiczp.bilibili.lotteryListener.event.SpecialGiftEndEvent
import com.hiczp.bilibili.lotteryListener.event.SpecialGiftStartEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

class NormalRoomListener(private val applicationContext: ApplicationContext) {
    @Subscribe
    fun onSpecialGift(specialGiftPackageEvent: SpecialGiftPackageEvent) {
        val specialGift = specialGiftPackageEvent.entity.data.`$39`
        logger.info("Received specialGift package, " +
                "roomId ${specialGiftPackageEvent.source0.roomIdOrShowRoomId}, id ${specialGift.id}, action '${specialGift.action}'")
        val event = when (specialGift.action) {
            "start" -> SpecialGiftStartEvent(specialGiftPackageEvent.source0, specialGiftPackageEvent.entity)
            "end" -> SpecialGiftEndEvent(specialGiftPackageEvent.source0, specialGiftPackageEvent.entity)
            else -> {
                logger.error("Received SPECIAL_GIFT package with unknown action '${specialGift.action}'")
                return
            }
        }
        applicationContext.publishEvent(event)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(NormalRoomListener::class.java)
    }
}
