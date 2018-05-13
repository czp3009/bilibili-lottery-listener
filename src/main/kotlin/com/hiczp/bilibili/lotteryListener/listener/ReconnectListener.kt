package com.hiczp.bilibili.lotteryListener.listener

import com.google.common.eventbus.Subscribe
import com.hiczp.bilibili.api.live.socket.event.ConnectionCloseEvent
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class ReconnectListener(private val reconnectTryLimit: Int = 0) {
    var userWantExit: Boolean = false

    @Subscribe
    fun onConnectionClose(connectionCloseEvent: ConnectionCloseEvent) {
        val liveClient = connectionCloseEvent.source0
        if (userWantExit) {
            logger.info("Connection with room ${liveClient.showRoomIdOrRoomId} closed")
            return
        }

        logger.error("Connection with room ${liveClient.showRoomIdOrRoomId} lost")
        if (reconnectTryLimit <= 0) return

        logger.info("Preparing to reconnect...")
        liveClient.unregisterListeners(this)
        //连接操作不能在 EventLoopThread 进行
        thread(true, true, block = {
            outside@ while (true) {
                for (i in 1..reconnectTryLimit) {
                    logger.info("Reconnecting... [$i/$reconnectTryLimit]")
                    try {
                        connectionCloseEvent.source0.connect()
                        break@outside
                    } catch (e: Exception) {
                        if (userWantExit) { //用户关闭了连接池并且不想重连
                            return@thread
                        }
                        logger.error("Reconnect failed")
                        val sleepTime =
                                if (i != reconnectTryLimit) {   //非最后一次失败
                                    logger.info("Delay ${RECONNECT_INTERVAL}s for next try")
                                    RECONNECT_INTERVAL * 1000
                                } else { //最后一次失败
                                    logger.error("All reconnection attempt failed, please check your network. " +
                                            "We will sleep ${SLEEP_TIME_AFTER_RECONNECT_ATTEMPT_ALL_FAILED}min before next try")
                                    SLEEP_TIME_AFTER_RECONNECT_ATTEMPT_ALL_FAILED * 1000 * 60
                                }
                        try {
                            Thread.sleep(sleepTime)
                        } catch (e: InterruptedException) {
                            return@thread
                        }
                    }
                }
            }
            liveClient.registerListener(this)
            logger.info("Reconnect complete")
        })
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ReconnectListener::class.java)
        /**
         * 重连间隔, 秒
         */
        private const val RECONNECT_INTERVAL = 2L
        /**
         * 在全部的连接重试结束后等待的时间, 分
         */
        private const val SLEEP_TIME_AFTER_RECONNECT_ATTEMPT_ALL_FAILED = 1L
    }
}
