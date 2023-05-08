package com.github.squirrelgrip.build.extension

import com.github.squirrelgrip.build.common.infra.DataStorage
import com.github.squirrelgrip.build.common.infra.DiskDataStorage
import com.github.squirrelgrip.build.common.model.Project
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import java.io.Closeable

class DataWriter(
    private val sessionProfile: SessionProfile,
    private var project: Project,
    private val aux: DataStorage = DiskDataStorage()
): Closeable {
    constructor(sessionProfile: SessionProfile): this(sessionProfile, sessionProfile.project)

    fun open() {
        aux.open()
        aux.updateSessionSummary(getSessionSummary())
    }

    fun checkPoint() {
        aux.updateSessionProfile(sessionProfile)
    }

    override fun close() {
        aux.updateProjectSummary(ProjectSummary(project, getSessionSummary()))
        aux.updateSessionSummary(getSessionSummary())
        aux.updateSessionProfile(sessionProfile)
    }

    private fun getSessionSummary() = SessionSummary(sessionProfile)
}
