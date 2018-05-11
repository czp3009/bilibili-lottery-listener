package com.hiczp.bilibili.lotteryListener

import com.hiczp.bilibili.lotteryListener.config.LotteryListenerConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(LotteryListenerConfigurationProperties::class)
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
