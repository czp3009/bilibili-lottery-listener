# Bilibili-Lottery-Listener
Bilibili 抽奖监听服务器. 当 B站(直播) 有抽奖发生时, 将把这个消息推送到订阅者.

# 部署和运行

    apt install openjdk-8-jdk
    
然后需要通过配置文件指定数据库连接参数, 在程序工作目录创建配置文件

    touch application.properties

以下是必要的配置项

    spring.datasource.url=jdbc:mysql://localhost:3306/bilibili_lottery_listener
    spring.datasource.username=debian-sys-maint
    spring.datasource.password=

然后直接编译并启动程序

    ./gradlew bootRun

或者打包后再运行

    ./gradlew bootJar
    java -jar ./build/libs/bilibili-lottery-listener-{version}.jar

# 抽奖消息
## 全站通告的抽奖信息
全站通告的抽奖信息有  小电视, 20 倍以上的节奏风暴, 活动礼物

## 非全站通告的抽奖信息
比如 20 倍以下的节奏风暴不会在全站通告, 而只在发生这个事件的直播间内会有消息(在弹幕推送 socket 中).

对于这种事件, 本程序的策略是连接最热门的前 900 个直播间并监听他们的消息, 从而尽可能多的获得这种抽奖消息并推送给订阅者.

# 订阅
## Hook
通过注册一个 Hook 的方式, 来接收推送, 请求示例:

(注意这个 DanMuMsg 事件在生产环境中是不接收的)

POST http://localhost:8080/test/hook/danMuMsg

BODY application/json

    {"roomId":3,"realRoomId":23058,"eventType":"DANMU_MSG_EVENT","payload":{"cmd":"DANMU_MSG","info":[[0,1,25,16777215,1525934492,"1525934492",0,"8a0f75dc",0],"悄悄地奉献。",[39042255,"夏沫丶琉璃浅梦",0,1,0,10000,1,""],[15,"夏沫","乄夏沫丶","1547306",16746162,""],[44,0,16746162,5811],["task-year","title-29-1"],0,0,{"uname_color":""}]}}

//TODO
Hook 的注册还没实现, 现在要手动将 Hook 添加到数据库

事件类型

    enum class EventType {
        /**
         * DanMuMsg 用于测试时调试程序逻辑
         */
        DANMU_MSG_EVENT,
        /**
         * 小电视
         */
        SMALL_TV_EVENT,
        /**
         * 超过 20 倍的节奏风暴
         */
        GLOBAL_SPECIAL_GIFT_EVENT,
        /**
         * 活动礼物
         */
        ACTIVITY_GIFT_EVENT,
        /**
         * 节奏风暴开始(房间内通告的低于 20 倍的节奏风暴, 下同)
         */
        SPECIAL_GIFT_START_EVENT,
        /**
         * 节奏风暴结束
         */
        SPECIAL_GIFT_END_EVENT
    }

## WebSocket
客户端通过 WebSocket 连接到本程序, 并从 WebSocket 中得到推送

//TODO
尚未实现
