package com.hiczp.bilibili.lotteryListener.config

import com.hiczp.bilibili.api.BilibiliAPI
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class BeanDefinition {
    @Bean
    fun anonymousBilibiliAPI() = BilibiliAPI()
}
