package com.github.squirrelgrip.build.common.infra

interface DataWriter: AutoCloseable {
    fun open()
    fun checkPoint()
}
