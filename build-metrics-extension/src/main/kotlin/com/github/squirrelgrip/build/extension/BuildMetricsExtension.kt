package com.github.squirrelgrip.build.extension

import com.github.squirrelgrip.build.common.infra.DataWriter
import com.github.squirrelgrip.build.common.infra.DiskDataWriter
import com.github.squirrelgrip.build.common.model.Mojo
import com.github.squirrelgrip.build.common.model.MojoProfile
import com.github.squirrelgrip.build.common.model.Project
import com.github.squirrelgrip.build.common.model.ProjectProfile
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.Type
import com.github.squirrelgrip.build.common.model.Type.FAILED
import com.github.squirrelgrip.build.common.model.Type.PENDING
import com.github.squirrelgrip.build.common.model.Type.SUCCEEDED
import org.apache.maven.eventspy.AbstractEventSpy
import org.apache.maven.execution.ExecutionEvent
import org.apache.maven.execution.ExecutionEvent.Type.ForkFailed
import org.apache.maven.execution.ExecutionEvent.Type.ForkSucceeded
import org.apache.maven.execution.ExecutionEvent.Type.ForkedProjectFailed
import org.apache.maven.execution.ExecutionEvent.Type.ForkedProjectSucceeded
import org.apache.maven.execution.ExecutionEvent.Type.MojoFailed
import org.apache.maven.execution.ExecutionEvent.Type.MojoSucceeded
import org.apache.maven.execution.ExecutionEvent.Type.ProjectFailed
import org.apache.maven.execution.ExecutionEvent.Type.ProjectSucceeded
import org.apache.maven.execution.MavenExecutionRequest
import org.apache.maven.execution.MavenExecutionResult
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.DependencyResolutionRequest
import org.apache.maven.project.DependencyResolutionResult
import org.apache.maven.project.MavenProject
import org.apache.maven.settings.building.SettingsBuildingRequest
import org.apache.maven.settings.building.SettingsBuildingResult
import org.apache.maven.toolchain.building.ToolchainsBuildingRequest
import org.apache.maven.toolchain.building.ToolchainsBuildingResult
import org.eclipse.aether.RepositoryEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Named
import javax.inject.Singleton

@Named
@Singleton
class BuildMetricsExtension(
    private val dataStorageFactory: (SessionProfile) -> DataWriter
) : AbstractEventSpy() {
    constructor() : this({ DiskDataWriter(it) })

    private lateinit var sessionProfile: SessionProfile
    private lateinit var rootMavenProject: MavenProject
    private lateinit var dataWriter: DataWriter
    private var lastCheckPoint: Instant = Instant.EPOCH

    private val threadIndexGenerator = AtomicInteger()
    private val threadIndex = ThreadLocal.withInitial { threadIndexGenerator.incrementAndGet() }

    companion object {
        private val REGEX = ".*/".toRegex()
        private val LOGGER: Logger = LoggerFactory.getLogger(BuildMetricsExtension::class.java)
        val hostname: String =
            try {
                InetAddress.getLocalHost().hostName
            } catch (e: UnknownHostException) {
                "unknown"
            }
    }

    override fun onEvent(event: Any) {
        when (event) {
            is RepositoryEvent -> {}
            is ExecutionEvent -> onExecutionEvent(event)
            is DependencyResolutionRequest -> {}
            is DependencyResolutionResult -> {}
            is MavenExecutionRequest -> {}
            is MavenExecutionResult -> {
                rootMavenProject = event.project
            }

            is SettingsBuildingRequest -> {}
            is SettingsBuildingResult -> {}
            is ToolchainsBuildingRequest -> {}
            is ToolchainsBuildingResult -> {}
            else -> println("UNKNOWN EVENT ${event.javaClass}")
        }
    }

    private fun onExecutionEvent(executionEvent: ExecutionEvent) {
        when (executionEvent.type) {
            ExecutionEvent.Type.SessionStarted -> {
                val session: MavenSession = executionEvent.session
                val tag = getBranch(executionEvent.toMavenProject())
                sessionProfile = SessionProfile(
                    UUID.randomUUID().toString(),
                    executionEvent.toProject(),
                    command(session.request),
                    hostname,
                    System.getProperty("user.name"),
                    session.goals,
                    tag,
                    getProjectProfiles(session)
                )
                sessionProfile.status.start()
                LOGGER.info(
                    "Creating Maven build scanner session profile {}#{}",
                    sessionProfile.project.id,
                    sessionProfile.id
                )

                dataWriter = dataStorageFactory.invoke(sessionProfile)
                dataWriter.open()
            }

            ExecutionEvent.Type.SessionEnded -> {
                val session: MavenSession = executionEvent.session
                if (session.result.hasExceptions()) {
                    sessionProfile.status.end(FAILED)
                } else {
                    sessionProfile.status.end(SUCCEEDED)
                }
                dataWriter.close()
                logResult(session)
            }

            ExecutionEvent.Type.ProjectStarted -> {
                sessionProfile
                    .getProjectProfile(executionEvent.toProject())
                    .status
                    .start()
            }

            ProjectSucceeded, ExecutionEvent.Type.ProjectFailed -> {
                sessionProfile
                    .getProjectProfile(executionEvent.toProject())
                    .status
                    .end(executionEvent.type.toStatusType())
                maybeCheckPoint()
            }

            ExecutionEvent.Type.MojoStarted -> {
                val mojoExecution: MojoExecution = executionEvent.mojoExecution
                val projectProfile: ProjectProfile = sessionProfile.getProjectProfile(
                    executionEvent.toProject()
                )
                val mojoProfile = MojoProfile(
                    mojoExecution.toMojo(),
                    mojoExecution.executionId,
                    mojoExecution.goal,
                    threadIndex.get()
                )
                mojoProfile.status.start()
                LOGGER.debug("Started: $mojoProfile")
                projectProfile.addMojoProfile(mojoProfile)
            }

            ExecutionEvent.Type.MojoSucceeded, ExecutionEvent.Type.MojoFailed -> {
                val mojoExecution: MojoExecution = executionEvent.mojoExecution
                val mojoProfile = sessionProfile.getProjectProfile(
                    executionEvent.toProject()
                ).getMojoProfile(
                    mojoExecution.toMojo(),
                    mojoExecution.executionId,
                    mojoExecution.goal
                )
                mojoProfile.status.end(executionEvent.type.toStatusType())
                LOGGER.debug("End: $mojoProfile")
            }

            else -> {}
        }
    }

    private fun getProjectProfiles(session: MavenSession) =
        session.projectDependencyGraph.sortedProjects
            .map {
                it.toProject()
            }.associate {
                it.id to ProjectProfile(it)
            }

    private fun MojoExecution.toMojo(): Mojo =
        Mojo(groupId, artifactId, version)

    private fun MavenProject.toProject(): Project =
        Project(groupId, artifactId, version)

    private fun ExecutionEvent.toProject() =
        this.toMavenProject().toProject()

    private fun ExecutionEvent.toMavenProject() =
        this.project ?: rootMavenProject

    private fun ExecutionEvent.Type.toStatusType(): Type =
        when (this) {
            ProjectSucceeded -> SUCCEEDED
            ProjectFailed -> FAILED
            MojoSucceeded -> SUCCEEDED
            MojoFailed -> FAILED
            ForkSucceeded -> SUCCEEDED
            ForkFailed -> FAILED
            ForkedProjectSucceeded -> SUCCEEDED
            ForkedProjectFailed -> FAILED
            else -> PENDING
        }

    private fun logResult(session: MavenSession) {
        // val mavenHome = session.systemProperties.getProperty("maven.home")
        // LOGGER.info("To view your build scan, start-up the server:")
        // LOGGER.info(
        //     "java -jar {}",
        //     (mavenHome
        //         + File.separator
        //         + "lib"
        //         + File.separator
        //         + "ext"
        //         + File.separator
        //         + "maven-build-scanner-jar-with-dependencies.jar")
        // )
        // LOGGER.info(
        //     "Then open http://localhost:3000/?projectId={}&sessionId={}",
        //     sessionProfile.project.id,
        //     sessionProfile.id
        // )
    }

    private fun maybeCheckPoint() {
        if (Duration.between(lastCheckPoint, Instant.now()).seconds > 30) {
            LOGGER.info("Requesting check-point")
            dataWriter.checkPoint()
            lastCheckPoint = Instant.now()
        }
    }

    private fun getBranch(mavenProject: MavenProject): String =
        getGitHead(mavenProject).let {
            if (!it.exists()) {
                "&lt;git not found&gt;"
            } else {
                try {
                    Files.readAllLines(it.toPath())
                        .map {
                            it.replaceFirst(REGEX, "")
                        }
                        .first()
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }
        }

    private fun getGitHead(mavenProject: MavenProject): File {
        var basedir = mavenProject.basedir
        var gitHead: File
        do {
            gitHead = File(basedir, ".git/HEAD")
            basedir = basedir!!.parentFile
        } while (basedir != null && !gitHead.exists())
        return gitHead
    }

    private fun command(request: MavenExecutionRequest): String =
        listOf(
            "mvn",
            "-s ${request.userSettingsFile}",
            "-T ${request.degreeOfConcurrency}",
            *request.activeProfiles.map { "-P$it" }.toTypedArray(),
            *request.userProperties.map { "-D${it.key}=${it.value}" }.toTypedArray(),
            *request.goals.toTypedArray()
        ).joinToString(" ")
}

