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
    @JsonIgnore
    private val profiles: Map<Project, ProjectProfile> = emptyMap(),
    val status: Status = Status()
) {
    val projectProfiles: Collection<ProjectProfile>
        get() = profiles.values

    fun getProjectProfile(project: Project): ProjectProfile =
        profiles[project]!!
}