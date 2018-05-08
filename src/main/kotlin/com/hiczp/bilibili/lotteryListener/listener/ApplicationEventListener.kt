package com.hiczp.bilibili.lotteryListener.listener

import com.hiczp.bilibili.lotteryListener.service.PrimaryWorkerService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ApplicationEventListener(private val primaryWorkerService: PrimaryWorkerService) {
    @EventListener(ApplicationReadyEvent::class)
    fun startPrimaryWorker() = primaryWorkerService.start()
}
