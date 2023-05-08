package com.github.squirrelgrip.build.common.infra

import com.github.squirrelgrip.build.common.model.Project
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary

interface DataStorage {
    fun open()
    fun updateProjectSummary(projectSummary: ProjectSummary)
    fun updateSessionSummary(sessionSummary: SessionSummary)
    fun updateSessionProfile(sessionProfile: SessionProfile)
    fun getProjectSummaries(): List<ProjectSummary>
    fun getSessionSummaries(groupId: String, artifactId: String): List<SessionSummary>
    fun getSessionProfile(id: String): SessionProfile
}