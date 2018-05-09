package com.hiczp.bilibili.lotteryListener.service

import com.hiczp.bilibili.api.BilibiliAPI
import com.hiczp.bilibili.lotteryListener.listener.PrimaryRoomListener
import com.hiczp.bilibili.lotteryListener.listener.PrimaryRoomTestListener
import com.hiczp.bilibili.lotteryListener.listener.ReconnectListener
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.stereotype.Service
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class PrimaryWorkerService(private val bilibiliAPI: BilibiliAPI,
                           private val applicationContext: ApplicationContext,
                           private val environment: ConfigurableEnvironment) {
    /**
     *  主 EventLoopGroup 用于连接 3 号直播间, 接收全网广播信息
     */
    private var eventLoopGroup: EventLoopGroup? = null
    private var reconnectListener: ReconnectListener? = null

    @PostConstruct
    fun onCreate() {
        eventLoopGroup = NioEventLoopGroup(1)
    }

    /**
     * 这个方法是同步的, Spring 初始化时抛出异常会终止初始化
     */
    fun start() {
        logger.info("Start connect to official music room...")
        try {
            reconnectListener = ReconnectListener(RECONNECT_TRY_LIMIT)
            val liveClient = bilibiliAPI.getLiveClient(eventLoopGroup, OFFICIAL_MUSIC_ROOM_ID)
                    .registerListener(reconnectListener!!)
                    .registerListener(PrimaryRoomListener(applicationContext))
            //开发模式接收 DanMuMsg
            if (environment.activeProfiles.contains("dev")) {
                liveClient.registerListener(PrimaryRoomTestListener(applicationContext))
            }
            liveClient.connect()
        } catch (e: IOException) {
            logger.error("Connect to official music room failed: ${e.message}")
            logger.error("There are some problem with your network, application init failed")
            throw e
        }
        logger.info("Connect to official music room complete")
    }

    @PreDestroy
    fun onDestroy() {
        reconnectListener?.userWantExit = true
        eventLoopGroup?.shutdownGracefully()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PrimaryWorkerService::class.java)
        private const val OFFICIAL_MUSIC_ROOM_ID = 3L
        private const val RECONNECT_TRY_LIMIT = 5
    }
}
