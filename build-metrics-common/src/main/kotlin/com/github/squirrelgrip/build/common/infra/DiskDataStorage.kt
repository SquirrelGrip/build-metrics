package com.github.squirrelgrip.build.common.infra

import com.github.squirrelgrip.build.common.model.Project
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import com.github.squirrelgrip.extension.json.toInstance
import com.github.squirrelgrip.extension.json.toJson
import java.io.File

class DiskDataStorage : DataStorage {
    companion object {
        private val RESULTS = File(File(System.getProperty("user.home"), ".mvn"), "scans")
        private val SESSION_SUMMARIES = File(RESULTS, "session-summaries")
        private val PROJECT_SUMMARIES = File(RESULTS, "project-summaries")
        private val SESSION_PROFILES = File(RESULTS, "session-profiles")
    }

    override fun open() {
        SESSION_PROFILES.mkdirs()
        PROJECT_SUMMARIES.mkdirs()
        SESSION_SUMMARIES.mkdirs()
    }

    override fun updateProjectSummary(projectSummary: ProjectSummary) {
        projectSummary.toJson(
            File(PROJECT_SUMMARIES, "${projectSummary.groupId}-${projectSummary.artifactId}.json")
        )
    }

    override fun updateSessionSummary(sessionSummary: SessionSummary) {
        sessionSummary.toJson(
            File(SESSION_SUMMARIES, "${sessionSummary.id}.json")
        )
    }

    override fun updateSessionProfile(sessionProfile: SessionProfile) {
        sessionProfile.toJson(
            File(SESSION_PROFILES, "${sessionProfile.id}.json")
        )
    }

    override fun getProjectSummaries(): List<ProjectSummary> =
        PROJECT_SUMMARIES.listFiles()?.map {
            it.toInstance()
        } ?: emptyList()

    override fun getSessionSummaries(groupId: String, artifactId: String): List<SessionSummary> =
        SESSION_SUMMARIES.listFiles()?.map {
            it.toInstance<SessionSummary>()
        }?.filter {
            it.project.groupId == groupId && it.project.artifactId == artifactId
        } ?: emptyList()

    override fun getSessionProfile(id: String): SessionProfile =
        File(SESSION_PROFILES, "$id.json").toInstance()
}