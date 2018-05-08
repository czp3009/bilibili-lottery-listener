package com.hiczp.bilibili.lotteryListener.listener

import com.hiczp.bilibili.lotteryListener.dao.HookRepository
import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import com.hiczp.bilibili.lotteryListener.service.EventPushService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class LotteryEventListener(private val eventPushService: EventPushService,
                                private val hookRepository: HookRepository) {
    @EventListener(LotteryEvent::class)
    fun pushToHooks(lotteryEvent: LotteryEvent) {
        logger.info("Preparing push ${lotteryEvent.eventType} to hooks")
        eventPushService.push(lotteryEvent, hookRepository.findByEventType(lotteryEvent.eventType))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LotteryEventListener::class.java)
    }
}
