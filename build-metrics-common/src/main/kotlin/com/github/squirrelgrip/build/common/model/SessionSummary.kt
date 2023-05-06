package com.github.squirrelgrip.build.common.model

data class SessionSummary(
    val id: String,
    val project: Project,
    val branch: String,
    val username: String,
    val goals: List<String>,
    val hostname: String,
    val status: Status
) {
    constructor(sessionProfile: SessionProfile) : this(
        sessionProfile.id,
        sessionProfile.project,
        sessionProfile.branch,
        sessionProfile.username,
        sessionProfile.goals,
        sessionProfile.hostname,
        sessionProfile.status
    )
}