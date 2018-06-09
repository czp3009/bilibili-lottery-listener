package com.hiczp.bilibili.lotteryListener.hook.controller

import com.hiczp.bilibili.lotteryListener.hook.dao.Hook
import com.hiczp.bilibili.lotteryListener.hook.dao.HookRepository
import com.hiczp.bilibili.lotteryListener.hook.model.HookCreationModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/hooks")
class HookController(private val hookRepository: HookRepository) {
    @GetMapping
    fun getHooks(pageable: Pageable): Page<Hook> = hookRepository.findAll(pageable)

    @PostMapping
    fun createHook(@Valid @RequestBody hookCreationModel: HookCreationModel): Hook =
            hookRepository.findByEventTypeAndUrl(hookCreationModel.eventType, hookCreationModel.url)
                    .ifPresent { throw DuplicateHookException() }
                    .run { hookRepository.save(hookCreationModel.toHook()) }

    @DeleteMapping("/{id}")
    fun deleteHookById(@PathVariable id: Long) =
            hookRepository.findById(id)
                    .orElseThrow { HookNotFoundException() }
                    .run { hookRepository.deleteById(id) }
}

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "The Hook already exists")
class DuplicateHookException : RuntimeException()

@ResponseStatus(HttpStatus.NOT_FOUND, reason = "Request Hook not exists")
class HookNotFoundException : RuntimeException()
