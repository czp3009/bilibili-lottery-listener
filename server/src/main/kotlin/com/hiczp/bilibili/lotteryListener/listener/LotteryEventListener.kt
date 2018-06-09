package com.hiczp.bilibili.lotteryListener.listener

import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import com.hiczp.bilibili.lotteryListener.service.WebSocketNotifyService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("webSocketLotteryEventListener")
open class LotteryEventListener(private val webSocketNotifyService: WebSocketNotifyService) {
    @EventListener(LotteryEvent::class)
    fun pushToWebSocketClients(lotteryEvent: LotteryEvent<*>) {
        logger.info("Preparing notify ${lotteryEvent.eventType} to WebSocket clients")
        webSocketNotifyService.notify(lotteryEvent)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LotteryEventListener::class.java)
    }
}
