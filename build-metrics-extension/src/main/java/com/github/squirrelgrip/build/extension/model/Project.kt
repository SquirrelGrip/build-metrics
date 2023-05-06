package com.github.squirrelgrip.build.extension.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.maven.project.MavenProject

@JsonIgnoreProperties(ignoreUnknown = true)
data class Project(
    val groupId: String,
    val artifactId: String,
    val version: String
) {
    @get:JsonIgnore
    val id: String
        get() = "$groupId:$artifactId"

    constructor(mavenProject: MavenProject): this(
        mavenProject.groupId, mavenProject.artifactId, mavenProject.version
    )

    override fun equals(other: Any?): Boolean =
        if (other is Project) EssentialProject(this) == EssentialProject(other) else false
    override fun hashCode(): Int =
        EssentialProject(this).hashCode()
    override fun toString(): String =
        EssentialProject(this).toString().replace("EssentialProject", "Project")
}

private data class EssentialProject(
    val groupId: String,
    val artifactId: String
) {
    constructor(project: Project): this(project.groupId, project.artifactId)
}
