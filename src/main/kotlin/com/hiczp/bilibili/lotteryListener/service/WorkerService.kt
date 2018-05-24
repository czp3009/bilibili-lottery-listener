package com.hiczp.bilibili.lotteryListener.service

import com.google.common.util.concurrent.RateLimiter
import com.hiczp.bilibili.api.BilibiliAPI
import com.hiczp.bilibili.api.live.entity.RoomsEntity
import com.hiczp.bilibili.lotteryListener.config.LotteryListenerConfigurationProperties
import com.hiczp.bilibili.lotteryListener.listener.NormalRoomListener
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import javax.annotation.PreDestroy

@Service
class WorkerService(private val bilibiliAPI: BilibiliAPI,
                    private val normalRoomListeners: List<NormalRoomListener>,
                    private val executorService: ExecutorService,
                    private val lotteryListenerConfigurationProperties: LotteryListenerConfigurationProperties) {
    private val rateLimiter = RateLimiter.create(lotteryListenerConfigurationProperties.requestRateLimit)
    private val pageCount = lotteryListenerConfigurationProperties.pageCount
    private val expectedRoomCount = pageCount * PAGE_SIZE
    private var eventLoopGroup: EventLoopGroup? = null

    private fun connectToHottestRooms() {
        logger.info("Start connect to top $expectedRoomCount hottest room")
        //获取前 x 页的热门房间信息
        val countDownLatch = CountDownLatch(pageCount)
        val rooms = ArrayList<RoomsEntity.Data>()
        for (page in 1..pageCount) {
            rateLimiter.acquire()
            bilibiliAPI.liveService.getHottestRooms(page).enqueue(object : Callback<RoomsEntity> {
                override fun onResponse(call: Call<RoomsEntity>, response: Response<RoomsEntity>) {
                    run {
                        val code = response.code()
                        if (code != HttpStatus.OK.value()) {
                            logger.error("Fetch hottest room page $page with return code $code")
                            return@run
                        }
                        val body = response.body()!!
                        if (body.code != 0) {
                            logger.error("Error occurred while fetching hottest room page $page: ${body.message}")
                            return@run
                        }
                        rooms.addAll(body.data.filter { it != null }) //我不知道为什么, 但是测试中存在 NullPointerException 的情况
                        logger.debug("Fetch hottest room page $page complete")
                    }
                    countDownLatch.countDown()
                }

                override fun onFailure(call: Call<RoomsEntity>, t: Throwable) {
                    logger.error("Fetch hottest room page $page failed, message: ${t.message}")
                    countDownLatch.countDown()
                }
            })
        }

        //等待所有页面的房间信息请求完毕
        try {
            countDownLatch.await()
        } catch (e: InterruptedException) { //如果此时中断 ScheduleTaskThread, 则立即返回
            return
        }
        //如果热门房间列表在获取的途中发生了变动, 可能会导致最终结果里面有重复的房间
        val availableRooms = rooms.distinctBy { it.roomId }  //这一行可能会抛出 NullPointerException, 完全不知道为什么
        logger.info("Get ${availableRooms.size} available rooms")
        if (availableRooms.isEmpty()) {
            logger.error("Cannot get any available rooms, please check your network")
        }

        //开始连接房间
        logger.info("Start connect to ${availableRooms.size} rooms")
        availableRooms.forEach {
            if (executorService.isShutdown) return
            //限制每秒最大请求数, 以免被封
            rateLimiter.acquire()
            executorService.submit(
                    bilibiliAPI.getLiveClient(eventLoopGroup, it.roomId, true)
                            .apply { normalRoomListeners.forEach { registerListener(it) } }
                            .connectAsync()
            )
        }
        logger.info("All connection requests have been sent")
    }

    @PreDestroy
    fun onDestroy() {
        executorService.shutdownNow()
        eventLoopGroup?.shutdownGracefully()
    }

    fun refresh() {
        eventLoopGroup?.run {
            logger.info("Closing connections...")
            this.shutdownGracefully().sync()
            logger.info("Previous connections closed")
        }
        eventLoopGroup = NioEventLoopGroup(expectedRoomCount / lotteryListenerConfigurationProperties.roomsPerThread)
        connectToHottestRooms()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WorkerService::class.java)
        /**
         * 最热房间的 API 每页有 20 个元素
         */
        private const val PAGE_SIZE = 30
    }
}
