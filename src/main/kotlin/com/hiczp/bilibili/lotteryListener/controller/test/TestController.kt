package com.hiczp.bilibili.lotteryListener.controller.test

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
@RequestMapping("/test/hook")
class TestController {
    /**
     * 我们假装接受者是一种动态语言, 接收类型是字符串
     * 我们假装 hook 有返回值, 并且甚至不是一个 JSON
     */
    @PostMapping("/danMuMsg")
    fun hook(@RequestBody string: String): String {
        logger.debug("Received pushModel, JSON below: \n$string")
        return "HelloWorld!"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestController::class.java)
    }
}
