package com.github.squirrelgrip.build.common.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Mojo(
    val groupId: String,
    val artifactId: String,
    val version: String
) {
    override fun equals(other: Any?): Boolean =
        if (other is Mojo) EssentialMojo(this) == EssentialMojo(other) else false
    override fun hashCode(): Int =
        EssentialMojo(this).hashCode()
    override fun toString(): String =
        EssentialMojo(this).toString().replace("EssentialMojo", "Mojo")
}

private data class EssentialMojo(
    val groupId: String,
    val artifactId: String
) {
    constructor(mojo: Mojo): this(mojo.groupId, mojo.artifactId)
}
