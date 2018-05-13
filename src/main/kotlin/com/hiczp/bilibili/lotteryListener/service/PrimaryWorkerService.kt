package com.hiczp.bilibili.lotteryListener.service

import com.hiczp.bilibili.api.BilibiliAPI
import com.hiczp.bilibili.lotteryListener.config.LotteryListenerConfigurationProperties
import com.hiczp.bilibili.lotteryListener.listener.PrimaryRoomListener
import com.hiczp.bilibili.lotteryListener.listener.ReconnectListener
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class PrimaryWorkerService(private val bilibiliAPI: BilibiliAPI,
                           private val primaryRoomListeners: List<PrimaryRoomListener>,
                           private val lotteryListenerConfigurationProperties: LotteryListenerConfigurationProperties) {
    /**
     *  主 EventLoopGroup 用于连接 3 号直播间, 接收全网广播信息
     */
    private var eventLoopGroup: EventLoopGroup? = null
    private var reconnectListener: ReconnectListener? = null

    @PostConstruct
    fun onCreate() {
        eventLoopGroup = NioEventLoopGroup(1)
        reconnectListener = ReconnectListener(lotteryListenerConfigurationProperties.reconnectTryLimit)
    }

    /**
     * 这个方法是同步的, 如果连接不上 3 号直播间将终止程序
     */
    fun start() {
        logger.info("Start connect to official music room...")
        try {
            val liveClient = bilibiliAPI.getLiveClient(eventLoopGroup, OFFICIAL_MUSIC_ROOM_ID)
                    .registerListener(reconnectListener!!)
            primaryRoomListeners.forEach { liveClient.registerListener(it) }
            liveClient.connect()
        } catch (e: IOException) {
            logger.error("Connect to official music room failed: ${e.message}")
            logger.error("There are some problem with your network, application init failed")
            throw e //在主线程上抛出异常就可以终止 Spring
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
    }
}
