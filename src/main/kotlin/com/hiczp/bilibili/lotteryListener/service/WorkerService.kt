package com.hiczp.bilibili.lotteryListener.service

import com.google.common.util.concurrent.RateLimiter
import com.hiczp.bilibili.api.BilibiliAPI
import com.hiczp.bilibili.api.live.entity.RoomsEntity
import com.hiczp.bilibili.lotteryListener.listener.NormalRoomListener
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
open class WorkerService(private val bilibiliAPI: BilibiliAPI,
                         private val applicationContext: ApplicationContext) {
    private val rateLimiter = RateLimiter.create(REQUEST_RATE_LIMIT)
    private lateinit var normalRoomListener: NormalRoomListener
    private var eventLoopGroup: EventLoopGroup? = null

    @PostConstruct
    fun onCreate() {
        normalRoomListener = NormalRoomListener(applicationContext)
        createEventLoopGroup()
    }

    private fun createEventLoopGroup() {
        eventLoopGroup = NioEventLoopGroup(EXPECTED_ROOM_COUNT / 50) //每五十个房间使用一个线程
    }

    open fun connectToHottestRooms() {
        logger.info("Start connect to top $EXPECTED_ROOM_COUNT hottest room")
        //获取前 x 页的热门房间信息
        val countDownLatch = CountDownLatch(PAGE_COUNT)
        val rooms = ArrayList<RoomsEntity.Data>()
        for (page in 1..PAGE_COUNT) {
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
        rooms.distinctBy { it.roomId }
        logger.info("Get ${rooms.size} available rooms")

        //开始连接房间
        val actualConnectCount = AtomicInteger()
        rooms.forEach { connectToRoom(it, actualConnectCount) }

        logger.info("Expected connect to $EXPECTED_ROOM_COUNT rooms, ${rooms.size} available, $actualConnectCount succeed")
    }

    @Async
    open fun connectToRoom(room: RoomsEntity.Data, actualConnectCount: AtomicInteger) {
        //一秒钟最大连接 20 个, 以免被封
        rateLimiter.acquire()
        val roomId = room.roomId
        try {
            bilibiliAPI.getLiveClient(eventLoopGroup, roomId)
                    .registerListener(normalRoomListener)
                    .connect()
            actualConnectCount.incrementAndGet()
            logger.debug("Connect to room $roomId succeed")
        } catch (e: IOException) {
            logger.error("Connect to room $roomId failed: ${e.message}")
        }
    }

    @PreDestroy
    fun onDestroy() {
        shutdownEventLoopGroup()
    }

    private fun shutdownEventLoopGroup() = eventLoopGroup?.shutdownGracefully()

    open fun refresh() {
        shutdownEventLoopGroup()
        createEventLoopGroup()
        connectToHottestRooms()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WorkerService::class.java)
        /**
         * 连接前多少页的最热房间
         */
        private const val PAGE_COUNT = 30
        /**
         * 最热房间的 API 每页有 20 个元素
         */
        private const val PAGE_SIZE = 30
        /**
         * 需要连接的房间数量
         */
        private const val EXPECTED_ROOM_COUNT = PAGE_COUNT * PAGE_SIZE
        /**
         * 每秒最大请求数, 用于防止 B站 禁封 IP
         */
        const val REQUEST_RATE_LIMIT = 20.0
        /**
         * 断开连接并重新连接新的最热房间(复数)的间隔, 分
         */
        const val REFRESH_INTERVAL = 30L
    }
}
