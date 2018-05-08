package com.hiczp.bilibili.lotteryListener.controller

import com.hiczp.bilibili.lotteryListener.dto.PushModel
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 开发时自己测试用的控制器
 */
@Profile("dev")
@RestController
@RequestMapping("/test")
class TestController {
    /**
     * 我们假装 hook 有返回值, 并且甚至不是一个 JSON
     */
    @PostMapping("/hook")
    fun hook(@RequestBody pushModel: PushModel): String {
        logger.info("Received pushModel, type ${pushModel.eventType}")
        return "HelloWorld!"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestController::class.java)
    }
}
