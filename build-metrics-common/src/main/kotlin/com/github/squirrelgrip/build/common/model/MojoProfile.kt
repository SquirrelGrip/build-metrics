package com.github.squirrelgrip.build.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MojoProfile(
    val mojo: Mojo,
    val executionId: String,
    val goal: String,
    val threadIndex: Int,
    val status: Status = Status()
)