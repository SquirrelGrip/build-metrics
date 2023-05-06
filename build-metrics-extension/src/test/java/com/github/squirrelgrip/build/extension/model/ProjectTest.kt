package com.github.squirrelgrip.build.extension.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ProjectTest {
    val aA1 = Project("A", "a", "1")
    val aA2 = Project("A", "a", "2")
    val bA1 = Project("A", "b", "1")

    @Test
    fun testEquals() {
        assertThat(aA1).isEqualTo(aA1)
        assertThat(aA1.hashCode()).isEqualTo(aA1.hashCode())
        assertThat(aA1).isEqualTo(aA2)
        assertThat(aA1.hashCode()).isEqualTo(aA2.hashCode())
        assertThat(aA1).isNotEqualTo(bA1)
    }

    @Test
    fun testToString() {
        assertThat(aA1.toString()).isNotEqualTo("")
        assertThat(aA2.toString()).isNotEqualTo("")
        assertThat(bA1.toString()).isNotEqualTo("")
    }
}