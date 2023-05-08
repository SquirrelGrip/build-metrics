package com.github.squirrelgrip.buildmetricsserver2.controller

import com.github.squirrelgrip.build.common.infra.DataStorage
import com.github.squirrelgrip.build.common.infra.DataStorageOperations
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import org.springframework.http.MediaType
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

    @GetMapping("/api/v1/session-summaries/{projectId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getSessionSummaries(
        @PathVariable("projectId", required = true) projectId: String
    ): List<SessionSummary> =
        projectId.split(COLON_REGEX).let {
            dataStorage.getSessionSummaries(it[0], it[1])
        }

    @GetMapping("/api/v1/project-summaries", produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getProjectSummaries(): List<ProjectSummary> =
        dataStorage.getProjectSummaries()

    @GetMapping("/api/v1/session-profiles/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    override fun getSessionProfile(
        @PathVariable("id", required = true) id: String
    ): SessionProfile =
        dataStorage.getSessionProfile(id)

    @PostMapping("/api/v1/update-project-summary", consumes = [MediaType.APPLICATION_JSON_VALUE])
    override fun updateProjectSummary(
        @RequestBody projectSummary: ProjectSummary
    ) {
        dataStorage.updateProjectSummary(projectSummary)
    }

    @PostMapping("/api/v1/update-session-summary", consumes = [MediaType.APPLICATION_JSON_VALUE])
    override fun updateSessionSummary(
        @RequestBody sessionSummary: SessionSummary
    ) {
        dataStorage.updateSessionSummary(sessionSummary)
    }

    @PostMapping("/api/v1/update-session-profile", consumes = [MediaType.APPLICATION_JSON_VALUE])
    override fun updateSessionProfile(
        @RequestBody sessionProfile: SessionProfile
    ) {
        dataStorage.updateSessionProfile(sessionProfile)
    }
}