package com.hiczp.bilibili.lotteryListener.service

import com.google.gson.Gson
import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import com.hiczp.bilibili.lotteryListener.model.toPushModel
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class WebSocketNotifyService(private val simpMessagingTemplate: SimpMessagingTemplate) {
    fun notify(lotteryEvent: LotteryEvent<*>) {
        logger.info("Notifying ${lotteryEvent.eventType} to WebSocket clients...")
        simpMessagingTemplate.convertAndSend("/${lotteryEvent.eventType}", gson.toJson(lotteryEvent.toPushModel()))
        logger.info("Notify ${lotteryEvent.eventType} to WebSockets clients complete")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketNotifyService::class.java)
        private val gson = Gson()
    }
}
