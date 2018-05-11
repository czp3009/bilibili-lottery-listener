package com.hiczp.bilibili.lotteryListener.config

import com.hiczp.bilibili.api.BilibiliAPI
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class BeanDefinition {
    @Bean
    fun anonymousBilibiliAPI() = BilibiliAPI()

    @Bean
    fun taskScheduler() = ConcurrentTaskScheduler()

    @Bean
    fun executorService(): ExecutorService = Executors.newCachedThreadPool()
}
