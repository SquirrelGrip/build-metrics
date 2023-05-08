package com.github.squirrelgrip.buildmetricsserver2.controller

import com.github.squirrelgrip.build.common.infra.DataStorage
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectSummaryController(
    val dataStorage: DataStorage
) {
    companion object {
        private val COLON_REGEX = ":".toRegex()
    }

    @GetMapping("/api/v1/session-summaries/{projectId}")
    fun sessionSummary(
        @PathVariable("projectId")
        projectId: String
    ): List<SessionSummary> =
        projectId.split(COLON_REGEX).let {
            dataStorage.getSessionSummaries(it[0], it[1])
        }

    @GetMapping("/api/v1/project-summaries")
    fun projectSummaries(): List<ProjectSummary> =
        dataStorage.getProjectSummaries()

    @GetMapping("/api/v1/session-profiles/{id}")
    fun sessionProfile(
        @PathVariable("id")
        id: String
    ): SessionProfile =
        dataStorage.getSessionProfile(id)
}