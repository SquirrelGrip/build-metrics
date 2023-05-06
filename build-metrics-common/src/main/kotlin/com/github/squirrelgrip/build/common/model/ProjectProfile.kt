package com.github.squirrelgrip.build.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectProfile(
    val project: Project,
    val status: Status = Status()
) {
    val mojoProfiles: MutableList<MojoProfile> = mutableListOf()

    fun addMojoProfile(mojoProfile: MojoProfile) {
        mojoProfiles.add(mojoProfile)
    }

    fun getMojoProfile(mojo: Mojo, executionId: String, goal: String): MojoProfile =
        mojoProfiles.first {
            it.mojo == mojo
                && it.executionId == executionId
                && it.goal == goal
        }
}