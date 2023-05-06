package com.github.squirrelgrip.build.extension.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.maven.plugin.MojoExecution

@JsonIgnoreProperties(ignoreUnknown = true)
data class MojoProfile(
    val mojo: Mojo,
    val executionId: String,
    val goal: String,
    val threadIndex: Int,
    val status: Status = Status()
) {
    constructor(
        mojoExecution: MojoExecution,
        threadIndex: Int
    ) : this(
        Mojo(mojoExecution),
        mojoExecution.executionId,
        mojoExecution.goal,
        threadIndex
    )
}