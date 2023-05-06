package com.github.squirrelgrip.build.extension.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.squirrelgrip.build.extension.model.Project
import com.github.squirrelgrip.build.extension.model.ProjectSummary
import com.github.squirrelgrip.build.extension.model.SessionProfile
import com.github.squirrelgrip.build.extension.model.SessionSummary
import com.github.squirrelgrip.extension.json.toInstance
import com.github.squirrelgrip.extension.json.toJson
import java.io.File

class DiskDataStorage {
    companion object {
        private val RESULTS = File(File(System.getProperty("user.home"), ".mvn"), "scans")
        private val SESSION_SUMMARIES = File(RESULTS, "session-summaries")
        private val PROJECT_SUMMARIES = File(RESULTS, "project-summaries")
        private val SESSION_PROFILES = File(RESULTS, "session-profiles")
    }

    fun open() {
        SESSION_PROFILES.mkdirs()
        PROJECT_SUMMARIES.mkdirs()
        SESSION_SUMMARIES.mkdirs()
    }

    fun updateProjectSummary(project: Project, sessionProfile: SessionProfile) {
        ProjectSummary(project, SessionSummary(sessionProfile)).toJson(
            File(PROJECT_SUMMARIES, "${project.groupId}-${project.artifactId}.json")
        )
    }

    fun updateSessionSummary(sessionProfile: SessionProfile) {
        SessionSummary(sessionProfile).toJson(
            File(SESSION_SUMMARIES, "${sessionProfile.id}.json")
        )
    }

    fun updateSessionProfile(sessionProfile: SessionProfile) {
        sessionProfile.toJson(
            File(SESSION_PROFILES, "${sessionProfile.id}.json")
        )
    }

    fun listProjectSummaries(): List<ProjectSummary> =
        PROJECT_SUMMARIES.listFiles()?.map {
            it.toInstance()
        } ?: emptyList()

    fun listSessionSummaries(groupId: String?, artifactId: String?): List<SessionSummary> =
        SESSION_SUMMARIES.listFiles()?.map {
            it.toInstance<SessionSummary>()
        }?.filter {
            it.project.groupId == groupId && it.project.artifactId == artifactId
        } ?: emptyList()

    fun getSessionProfile(id: String): String =
        File(SESSION_PROFILES, "$id.json").readText()
}