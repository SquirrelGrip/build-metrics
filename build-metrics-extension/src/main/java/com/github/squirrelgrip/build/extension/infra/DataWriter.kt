package com.github.squirrelgrip.build.extension.infra

interface DataWriter: AutoCloseable {
    fun open()
    fun checkPoint()
}
