package com.hiczp.bilibili.lotteryListener.service

import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WebSocketNotifyService {
    fun notify(lotteryEvent: LotteryEvent<*>) {
        logger.info("Notifying ${lotteryEvent.eventType} to WebSocket clients...")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketNotifyService::class.java)
    }
}
