package com.github.squirrelgrip.build.common.infra

import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import feign.Body
import feign.Headers
import feign.Param
import feign.RequestLine
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface DataStorageOperations {
    @RequestLine("GET /api/v1/project-summaries")
    @GetMapping("/api/v1/project-summaries", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProjectSummaries(): List<ProjectSummary>

    @RequestLine("GET /api/v1/session-summaries/{projectId}")
    @GetMapping("/api/v1/session-summaries/{projectId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSessionSummaries(
        @PathVariable("projectId", required = true)
        @Param("projectId")
        projectId: String
    ): List<SessionSummary>

    @RequestLine("GET /api/v1/session-profiles/{id}")
    @GetMapping("/api/v1/session-profiles/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSessionProfile(
        @PathVariable("id", required = true)
        @Param("id")
        id: String
    ): SessionProfile

    @RequestLine("POST /api/v1/update-project-summary")
    @Headers("Content-Type: application/json")
    @PostMapping("/api/v1/update-project-summary", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectSummary(
        @RequestBody projectSummary: ProjectSummary
    )

    @RequestLine("POST /api/v1/update-session-summary")
    @Headers("Content-Type: application/json")
    @PostMapping("/api/v1/update-session-summary", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateSessionSummary(
        @RequestBody sessionSummary: SessionSummary
    )

    @RequestLine("POST /api/v1/update-session-profile")
    @Headers("Content-Type: application/json")
    @PostMapping("/api/v1/update-session-profile", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateSessionProfile(
        @RequestBody sessionProfile: SessionProfile
    )

}