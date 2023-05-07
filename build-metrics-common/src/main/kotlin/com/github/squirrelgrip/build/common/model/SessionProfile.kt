package com.github.squirrelgrip.build.common.model

import com.fasterxml.jackson.annotation.JsonIgnore

class SessionProfile(
    val id: String,
    val project: Project,
    val command: String,
    val hostname: String,
    val username: String,
    val goals: List<String>,
    val branch: String,
    val profiles: Map<String, ProjectProfile> = emptyMap(),
    val status: Status = Status()
) {
    fun getProjectProfile(project: Project): ProjectProfile =
        profiles[project.id]!!
}