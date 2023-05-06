package com.github.squirrelgrip.build.common.model

data class ProjectSummary(
    val groupId: String,
    val artifactId: String,
    val sessionSummary: SessionSummary
) {
    constructor(
        project: Project,
        sessionSummary: SessionSummary
    ) : this(
        project.groupId,
        project.artifactId,
        sessionSummary
    )
}
