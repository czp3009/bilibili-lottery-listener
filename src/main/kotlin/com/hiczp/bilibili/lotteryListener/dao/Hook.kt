package com.hiczp.bilibili.lotteryListener.dao

import com.hiczp.bilibili.lotteryListener.event.EventType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
@Table(
        indexes = [
            Index(columnList = "eventType"),
            Index(columnList = "eventType, url", unique = true)
        ]
)
data class Hook(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long,

//        @ManyToOne
//        var user: User,

        @Column(nullable = false, length = 32)
        @Enumerated(EnumType.STRING)
        var eventType: EventType,

        @Column(nullable = false)
        var url: String
)

@Repository
interface HookRepository : PagingAndSortingRepository<Hook, Long> {
    fun findByEventType(eventType: EventType): List<Hook>
}
