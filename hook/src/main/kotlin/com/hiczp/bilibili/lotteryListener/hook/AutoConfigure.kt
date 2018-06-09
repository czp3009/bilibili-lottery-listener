package com.hiczp.bilibili.lotteryListener.hook

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@ComponentScan("com.hiczp.bilibili.lotteryListener.hook")
@EntityScan("com.hiczp.bilibili.lotteryListener.hook.dao")
@EnableJpaRepositories("com.hiczp.bilibili.lotteryListener.hook.dao")
open class AutoConfigure
