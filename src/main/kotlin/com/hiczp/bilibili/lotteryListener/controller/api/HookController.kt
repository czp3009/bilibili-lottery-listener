package com.hiczp.bilibili.lotteryListener.controller.api

import com.hiczp.bilibili.lotteryListener.dao.Hook
import com.hiczp.bilibili.lotteryListener.dao.HookRepository
import com.hiczp.bilibili.lotteryListener.model.HookCreationModel
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
    fun createHook(@Valid @RequestBody hookCreationModel: HookCreationModel): Hook {
        hookRepository.findByEventTypeAndUrl(hookCreationModel.eventType, hookCreationModel.url).ifPresent { throw DuplicateHookException() }
        return hookRepository.save(hookCreationModel.toHook())
    }

    @DeleteMapping("/{id}")
    fun deleteHookById(@PathVariable id: Long) {
        hookRepository.findById(id).orElseThrow { HookNotFoundException() }
        hookRepository.deleteById(id)
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "The Hook already exists")
class DuplicateHookException : RuntimeException()

@ResponseStatus(HttpStatus.NOT_FOUND, reason = "Request Hook not exists")
class HookNotFoundException : RuntimeException()
