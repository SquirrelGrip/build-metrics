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
    private val dataStorage: DataStorage = DiskDataStorage()
): Closeable {
    constructor(sessionProfile: SessionProfile): this(sessionProfile, sessionProfile.project)

    fun open() {
        dataStorage.open()
        dataStorage.updateSessionSummary(getSessionSummary())
    }

    fun checkPoint() {
        dataStorage.updateSessionProfile(sessionProfile)
    }

    override fun close() {
        dataStorage.updateProjectSummary(ProjectSummary(project, getSessionSummary()))
        dataStorage.updateSessionSummary(getSessionSummary())
        dataStorage.updateSessionProfile(sessionProfile)
    }

    private fun getSessionSummary() = SessionSummary(sessionProfile)
}
