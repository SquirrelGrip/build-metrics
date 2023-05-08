package com.github.squirrelgrip.buildmetricsserver2

import com.github.squirrelgrip.build.common.infra.DataStorage
import com.github.squirrelgrip.build.common.infra.DiskDataStorage
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.context.annotation.Bean

@SpringBootApplication(exclude = [MongoAutoConfiguration::class])
class BuildMetricsServer2Application {
	@Bean
	fun dataStorage(): DataStorage =
		DiskDataStorage()
}

fun main(args: Array<String>) {
	SpringApplication.run(BuildMetricsServer2Application::class.java, *args)
}


