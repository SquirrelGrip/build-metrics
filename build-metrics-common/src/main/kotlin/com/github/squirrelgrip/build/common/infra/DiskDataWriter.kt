package com.github.squirrelgrip.build.common.infra

import com.github.squirrelgrip.build.common.model.Project
import com.github.squirrelgrip.build.common.model.SessionProfile

class DiskDataWriter(
    private val sessionProfile: SessionProfile,
    private var project: Project,
    private val aux: DiskDataStorage = DiskDataStorage()
): DataWriter {
    constructor(sessionProfile: SessionProfile): this(sessionProfile, sessionProfile.project)

    override fun open() {
        aux.open()
        aux.updateSessionSummary(sessionProfile)
    }

    override fun checkPoint() {
        aux.updateSessionProfile(sessionProfile)
    }

    override fun close() {
        aux.updateProjectSummary(project, sessionProfile)
        aux.updateSessionSummary(sessionProfile)
        aux.updateSessionProfile(sessionProfile)
    }
}
