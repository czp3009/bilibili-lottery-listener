# Bilibili-Lottery-Listener
Bilibili 抽奖监听服务器. 当 B站(直播) 有抽奖发生时, 将把这个消息推送到订阅者.

# 运行
安装 jre

    apt install openjdk-8-jre

在本仓库的 [releases](/releases) 页面下载 jar 后, 执行以下命令行

    java -jar server-{version}.jar

(version 是版本号)

# 构建
安装 jdk

    apt install openjdk-8-jdk
    
clone 本仓库后

    cd bilibili-lottery-listener
    ./gradlew bootJar

# 可配置项
配置文件需要放置在程序工作目录, 配置文件可以是 properties 也可以是 yaml.

配置写在配置文件中, 例如(也可以是 yaml)

    bilibili.listener.page-count=30

以下是默认值和注释(配置文件中使用短横线命名法)

    data class LotteryListenerConfigurationProperties(
            /**
             * 重新连接官方音悦台的重试次数限制
             */
            var reconnectTryLimit: Int = 5,
            /**
             * 连接前多少页的最热房间(一页有 30 个房间)
             */
            var pageCount: Int = 30,
            /**
             * 多少个房间使用一个线程
             */
            var roomsPerThread: Int = 50,
            /**
             * 每秒最大请求数, 用于防止 B站 禁封 IP
             */
            var requestRateLimit: Double = 20.0,
            /**
             * 断开连接并重新连接新的最热房间(复数)的间隔, 分
             */
            var refreshInterval: Long = 30L
    )

# 抽奖消息
## 全站通告的抽奖信息
全站通告的抽奖信息有 小电视(摩天大楼的数据包与小电视是一样的), 20 倍以上的节奏风暴, 活动礼物

## 非全站通告的抽奖信息
比如 20 倍以下的节奏风暴不会在全站通告, 而只在发生这个事件的直播间内会有消息(在弹幕推送 socket 中).

对于这种事件, 本程序的策略是连接最热门的前 N 个直播间并监听他们的消息, 从而尽可能多的获得这种抽奖消息并推送给订阅者.

# 事件
以上几种事件的定义如下

    enum class EventType {
        /**
         * DanMuMsg 用于测试时调试程序逻辑, 生产环境不存在此事件
         */
        DANMU_MSG_EVENT,
        /**
         * 小电视(摩天大楼也是这个)
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

# 订阅
客户端通过 WebSocket 连接到本程序, 并从 WebSocket 中得到推送.

应用层协议使用 STOMP, 以 JavaScript 为例(npm install stompjs):

    const Stomp = require('stompjs');
    
    const client = Stomp.overWS('ws://localhost:8080/notifications');
    client.connect({}, frame => {
        console.log('Connected: ' + frame);
        client.subscribe('/DANMU_MSG_EVENT', message => {
            console.log('Received message: \n' + message.body)
        });
    });

WebSocket 的 Endpoint 为 "/notifications", Destination 为事件类型.

返回的 Message.body 为 JSON 字符串.

    {
        "roomId": 3,
        "realRoomId": 23058,
        "eventType": "DANMU_MSG_EVENT",
        "payload": {"cmd":"DANMU_MSG","info":[[0,1,25,16777215,1525934492,"1525934492",0,"8a0f75dc",0],"悄悄地奉献。",[39042255,"夏沫丶琉璃浅梦",0,1,0,10000,1,""],[15,"夏沫","乄夏沫丶","1547306",16746162,""],[44,0,16746162,5811],["task-year","title-29-1"],0,0,{"uname_color":""}]}
    }

(注意 DanMuMsg 事件在生产环境中是不触发的)

(payload 为原始数据)

为了方便强类型语言接收该消息, 以下提供该模型的定义

    data class PushModel<T : DataEntity>(
            val roomId: Long,
            val realRoomId: Long,
            val eventType: EventType,
            val payload: T
    )

(payload 可以当字符串来反序列化)

# 测试
测试代码(不同语言的 stomp 使用范例)在 /test 文件夹中.

# 开源协议
GPL V3
