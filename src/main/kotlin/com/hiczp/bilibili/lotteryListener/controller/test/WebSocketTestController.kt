package com.hiczp.bilibili.lotteryListener.controller.test

import com.hiczp.bilibili.lotteryListener.event.EventType
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile("dev")
@RestController
@RequestMapping("/test/websocket")
class WebSocketTestController(private val simpMessagingTemplate: SimpMessagingTemplate) {
    /**
     * 访问一次发一个消息
     */
    @GetMapping
    fun sendMessage() {
        logger.debug("Sending message to WebSocket clients...")
        simpMessagingTemplate.convertAndSend("/${EventType.DANMU_MSG_EVENT}", "HelloWorld")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketTestController::class.java)
    }
}
