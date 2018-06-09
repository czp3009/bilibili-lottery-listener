package com.hiczp.bilibili.lotteryListener.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "bilibili.listener")
data class LotteryListenerConfigurationProperties(
        /**
        重新连接官方音悦台的重试次数限制
         */
        var reconnectTryLimit: Int = 5,
        /**
        连接前多少页的最热房间(一页有 30 个房间)
         */
        var pageCount: Int = 30,
        /**
        多少个房间使用一个线程
         */
        var roomsPerThread: Int = 50,
        /**
        每秒最大请求数, 用于防止 B站 禁封 IP
         */
        var requestRateLimit: Double = 20.0,
        /**
        断开连接并重新连接新的最热房间(复数)的间隔, 分
         */
        var refreshInterval: Long = 30L
)
