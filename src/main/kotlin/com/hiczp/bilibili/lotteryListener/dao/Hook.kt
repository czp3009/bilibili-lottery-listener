package com.hiczp.bilibili.lotteryListener.dao

import com.hiczp.bilibili.lotteryListener.event.EventType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
@Table(
        indexes = [
            Index(columnList = "eventType"),
            Index(columnList = "url, eventType", unique = true)
        ]
)
data class Hook(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long,

        @Column(nullable = false)
        var url: String,

        @Column(nullable = false, length = 32)
        @Enumerated(EnumType.STRING)
        var eventType: EventType
)

@Repository
interface HookRepository : CrudRepository<Hook, Long> {
    fun findByEventType(eventType: EventType): List<Hook>
}
