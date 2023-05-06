package com.github.squirrelgrip.build.extension.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.maven.plugin.MojoExecution

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectProfile(
    val project: Project,
    val status: Status = Status()
) {
    val mojoProfiles: MutableList<MojoProfile> = mutableListOf()

    fun addMojoProfile(mojoProfile: MojoProfile) {
        mojoProfiles.add(mojoProfile)
    }

    fun getMojoProfile(mojoExecution: MojoExecution): MojoProfile =
        mojoProfiles.first {
            it.mojo == Mojo(mojoExecution)
                && it.executionId == mojoExecution.executionId
                && it.goal == mojoExecution.goal
        }
}