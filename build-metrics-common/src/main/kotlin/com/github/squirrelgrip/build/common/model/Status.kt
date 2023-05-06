package com.github.squirrelgrip.build.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.squirrelgrip.build.common.model.Type.PENDING
import java.time.Duration
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class Status(
    var status: Type = PENDING,
) {
    var startTime: Instant = Instant.EPOCH
        private set
    var endTime: Instant = Instant.EPOCH
        private set
    val duration: Duration
        get() = Duration.between(startTime, endTime)

    override fun toString(): String {
        return "Status(status=$status, startTime=$startTime, endTime=$endTime, duration=$duration)"
    }

    fun start() {
        status = PENDING
        startTime = Instant.now()
        endTime = startTime
    }

    fun end(status: Type) {
        this.status = status
        endTime = Instant.now()
    }
}




