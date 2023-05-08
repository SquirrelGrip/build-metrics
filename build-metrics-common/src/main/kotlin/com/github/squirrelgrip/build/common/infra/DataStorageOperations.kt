package com.github.squirrelgrip.build.common.infra

import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import feign.Headers
import feign.Param
import feign.RequestLine

interface DataStorageOperations {
    @RequestLine("GET /api/v1/project-summaries")
    fun getProjectSummaries(): List<ProjectSummary>

    @RequestLine("GET /api/v1/session-summaries/{projectId}")
    fun getSessionSummaries(
        @Param("projectId")
        projectId: String
    ): List<SessionSummary>

    @RequestLine("GET /api/v1/session-profiles/{id}")
    fun getSessionProfile(
        @Param("id")
        id: String
    ): SessionProfile

    @RequestLine("POST /api/v1/update-project-summary")
    @Headers("Content-Type: application/json")
    fun updateProjectSummary(
        projectSummary: ProjectSummary
    )

    @RequestLine("POST /api/v1/update-session-summary")
    @Headers("Content-Type: application/json")
    fun updateSessionSummary(
        sessionSummary: SessionSummary
    )

    @RequestLine("POST /api/v1/update-session-profile")
    @Headers("Content-Type: application/json")
    fun updateSessionProfile(
        sessionProfile: SessionProfile
    )
}