package com.github.squirrelgrip.buildmetricsserver2.controller

import com.github.squirrelgrip.build.common.infra.DataStorage
import com.github.squirrelgrip.build.common.infra.DataStorageOperations
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectSummaryController(
    val dataStorage: DataStorage
) : DataStorageOperations {
    companion object {
        private val COLON_REGEX = ":".toRegex()
    }

    override fun getSessionSummaries(
        projectId: String
    ): List<SessionSummary> =
        projectId.split(COLON_REGEX).let {
            dataStorage.getSessionSummaries(it[0], it[1])
        }

    override fun getProjectSummaries(): List<ProjectSummary> =
        dataStorage.getProjectSummaries()

    override fun getSessionProfile(
        id: String
    ): SessionProfile =
        dataStorage.getSessionProfile(id)

    override fun updateProjectSummary(
        projectSummary: ProjectSummary
    ) {
        dataStorage.updateProjectSummary(projectSummary)
    }

    override fun updateSessionSummary(
        sessionSummary: SessionSummary
    ) {
        dataStorage.updateSessionSummary(sessionSummary)
    }

    override fun updateSessionProfile(
        sessionProfile: SessionProfile
    ) {
        dataStorage.updateSessionProfile(sessionProfile)
    }
}