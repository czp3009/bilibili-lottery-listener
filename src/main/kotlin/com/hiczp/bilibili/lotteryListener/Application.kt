package com.hiczp.bilibili.lotteryListener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
