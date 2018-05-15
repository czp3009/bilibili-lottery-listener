package com.hiczp.bilibili.lotteryListener.service

import com.hiczp.bilibili.lotteryListener.dao.Hook
import com.hiczp.bilibili.lotteryListener.event.LotteryEvent
import com.hiczp.bilibili.lotteryListener.model.PushModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

@Service
class HookPushService {
    private var httpService: HttpService

    init {
        httpService = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://localhost")
                .build()
                .create(HttpService::class.java)
    }

    fun push(lotteryEvent: LotteryEvent<*>, hooks: List<Hook>) {
        if (hooks.isEmpty()) {
            logger.info("No hook for ${lotteryEvent.eventType}")
            return
        }
        logger.info("Pushing ${lotteryEvent.eventType} to hooks...")
        hooks.forEach {
            httpService.push(it.url, PushModel(lotteryEvent)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>) {
                    logger.debug("${lotteryEvent.eventType} pushed to hook ${it.url} with return code ${response.code()}")
                }

                override fun onFailure(call: Call<Void>?, t: Throwable) {
                    logger.error("Error occurred while pushing ${lotteryEvent.eventType} to hook ${it.url}: ${t.message}")
                }
            })
        }
        logger.info("Push ${lotteryEvent.eventType} to hooks complete")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HookPushService::class.java)
    }

    private interface HttpService {
        //retrofit 使用泛型作为 body 好像有点问题
        @POST
        fun push(@Url url: String, @Body pushModel: Any): Call<Void>
    }
}
