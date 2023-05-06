package com.github.squirrelgrip.build.extension

import org.apache.maven.eventspy.AbstractEventSpy
import javax.inject.Named
import javax.inject.Singleton

@Named
@Singleton
class BuildMetricsExtension : AbstractEventSpy() {
    override fun onEvent(event: Any) {
        println(event)
    }
}