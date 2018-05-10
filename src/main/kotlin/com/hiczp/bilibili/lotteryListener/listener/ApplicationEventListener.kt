package com.hiczp.bilibili.lotteryListener.listener

import com.hiczp.bilibili.lotteryListener.service.PrimaryWorkerService
import com.hiczp.bilibili.lotteryListener.service.WorkerService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ApplicationEventListener(private val primaryWorkerService: PrimaryWorkerService,
                               private val workerService: WorkerService,
                               private val taskScheduler: TaskScheduler) {
    @Order(0)
    @EventListener(ApplicationReadyEvent::class)
    fun startPrimaryWorker() = primaryWorkerService.start()

    //由于 Spring boot 的 Scheduled 注解指定的任务会在触发 ApplicationReady 事件之前就被执行
    //导致程序不能被上面那个 service 在程序启动时正常终止, 所以这里采用手动的方式在程序启动后装填一个定时任务
    @Order(1)
    @EventListener(ApplicationReadyEvent::class)
    fun startWorker() {
        taskScheduler.scheduleWithFixedDelay(
                { workerService.refresh() },
                Duration.ofMinutes(WorkerService.REFRESH_INTERVAL)
        )
    }
}
