package com.github.squirrelgrip.buildmetricsserver2

import com.github.squirrelgrip.build.common.infra.DiskDataStorage
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(exclude = [MongoAutoConfiguration::class])
class BuildMetricsServer2Application {
	@Bean
	fun diskDataStorage(): DiskDataStorage =
		DiskDataStorage()

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<BuildMetricsServer2Application>(*args)
		}
	}

}

