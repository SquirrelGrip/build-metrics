package com.github.squirrelgrip.build.common.infra

import com.github.squirrelgrip.build.common.model.Project
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import com.github.squirrelgrip.extension.json.Json
import feign.Feign
import feign.hc5.ApacheHttp5Client
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder

class FeignDataStorage: DataStorage {
    private val url = "http://localhost:3000"

    private var dataStorageOperations: DataStorageOperations = Feign.builder()
        .client(ApacheHttp5Client())
        .encoder(JacksonEncoder(Json.objectMapper))
        .decoder(JacksonDecoder(Json.objectMapper))
        .target(DataStorageOperations::class.java, url)

    override fun open() {
    }

    override fun updateProjectSummary(projectSummary: ProjectSummary) {
        dataStorageOperations.updateProjectSummary(projectSummary)
    }

    override fun updateSessionSummary(sessionSummary: SessionSummary) {
        dataStorageOperations.updateSessionSummary(sessionSummary)
    }

    override fun updateSessionProfile(sessionProfile: SessionProfile) {
        dataStorageOperations.updateSessionProfile(sessionProfile)
    }

    override fun getProjectSummaries(): List<ProjectSummary> =
        dataStorageOperations.getProjectSummaries()

    override fun getSessionSummaries(groupId: String, artifactId: String): List<SessionSummary> =
        dataStorageOperations.getSessionSummaries(Project(groupId, artifactId, "").id)

    override fun getSessionProfile(id: String): SessionProfile =
        dataStorageOperations.getSessionProfile(id)
}