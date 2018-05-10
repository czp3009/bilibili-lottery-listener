package com.hiczp.bilibili.lotteryListener.config

import com.hiczp.bilibili.api.BilibiliAPI
import com.hiczp.bilibili.lotteryListener.service.WorkerService
import org.springframework.context.annotation.Bean
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class BeanDefinition {
    @Bean
    fun anonymousBilibiliAPI() = BilibiliAPI()

    @Bean
    fun taskScheduler() = ConcurrentTaskScheduler()

    @Bean
    fun taskExecutor(): TaskExecutor {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        val threadCount = WorkerService.REQUEST_RATE_LIMIT.toInt()
        threadPoolTaskExecutor.corePoolSize = threadCount
        threadPoolTaskExecutor.maxPoolSize = threadCount * 2
        return threadPoolTaskExecutor
    }
}
