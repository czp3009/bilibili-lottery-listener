package com.hiczp.bilibili.lotteryListener.listener

import com.hiczp.bilibili.lotteryListener.dao.HookRepository
import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import com.hiczp.bilibili.lotteryListener.service.HookPushService
import com.hiczp.bilibili.lotteryListener.service.WebSocketNotifyService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class LotteryEventListener(private val hookPushService: HookPushService,
                                private val webSocketNotifyService: WebSocketNotifyService,
                                private val hookRepository: HookRepository) {
    @EventListener(LotteryEvent::class)
    fun pushToHooks(lotteryEvent: LotteryEvent<*>) {
        logger.info("Preparing push ${lotteryEvent.eventType} to hooks")
        hookPushService.push(lotteryEvent, hookRepository.findByEventType(lotteryEvent.eventType))
    }

//    @EventListener(LotteryEvent::class)
//    fun pushToWebSockets(lotteryEvent: LotteryEvent<*>) {
//        logger.info("Preparing push ${lotteryEvent.eventType} to WebSockets")
//        webSocketNotifyService.notify(lotteryEvent)
//    }

    companion object {
        private val logger = LoggerFactory.getLogger(LotteryEventListener::class.java)
    }
}
