package com.github.squirrelgrip.build.extension.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.squirrelgrip.build.extension.model.Status.Type.*
import org.apache.maven.execution.ExecutionEvent
import org.apache.maven.execution.ExecutionEvent.Type.*
import java.time.Duration
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class Status(
    val initialStatus: Type = PENDING,
) {
    var startTime: Instant = Instant.EPOCH
        private set
    var endTime: Instant = Instant.EPOCH
        private set
    val duration: Duration
        get() = Duration.between(startTime, endTime)
    var status: Type = initialStatus
        private set

    fun start() {
        status = PENDING
        startTime = Instant.now()
        endTime = startTime
    }

    fun end(status: Type) {
        this.status = status
        endTime = Instant.now()
    }

    enum class Type {
        PENDING,
        SUCCEEDED,
        FAILED;
    }
}

fun ExecutionEvent.Type.toStatusType(): Status.Type =
    when(this) {
        ProjectSucceeded -> SUCCEEDED
        ProjectFailed -> FAILED
        MojoSucceeded -> SUCCEEDED
        MojoFailed -> FAILED
        ForkSucceeded -> SUCCEEDED
        ForkFailed -> FAILED
        ForkedProjectSucceeded -> SUCCEEDED
        ForkedProjectFailed -> FAILED
        else -> PENDING
    }


