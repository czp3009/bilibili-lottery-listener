package com.hiczp.bilibili.lotteryListener.hook.listener

import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import com.hiczp.bilibili.lotteryListener.hook.dao.HookRepository
import com.hiczp.bilibili.lotteryListener.hook.service.HookPushService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("hookLotteryEventListener")
open class LotteryEventListener(private val hookPushService: HookPushService,
                                private val hookRepository: HookRepository) {
    @EventListener(LotteryEvent::class)
    fun pushToHooks(lotteryEvent: LotteryEvent<*>) {
        logger.info("Preparing push ${lotteryEvent.eventType} to hooks")
        hookPushService.push(lotteryEvent, hookRepository.findByEventType(lotteryEvent.eventType))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LotteryEventListener::class.java)
    }
}
