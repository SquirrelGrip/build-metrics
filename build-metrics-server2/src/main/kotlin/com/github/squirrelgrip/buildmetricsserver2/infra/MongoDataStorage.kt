package com.github.squirrelgrip.buildmetricsserver2.infra

import com.github.squirrelgrip.build.common.infra.DataStorage
import com.github.squirrelgrip.build.common.model.ProjectSummary
import com.github.squirrelgrip.build.common.model.SessionProfile
import com.github.squirrelgrip.build.common.model.SessionSummary
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

class MongoDataStorage(
    val mongoTemplate: MongoTemplate
) : DataStorage {
    override fun open() {
        TODO("Not yet implemented")
    }

    override fun updateProjectSummary(projectSummary: ProjectSummary) {
        mongoTemplate.save(projectSummary)
    }

    override fun updateSessionSummary(sessionSummary: SessionSummary) {
        mongoTemplate.save(sessionSummary)
    }

    override fun updateSessionProfile(sessionProfile: SessionProfile) {
        mongoTemplate.save(sessionProfile)
    }

    override fun getProjectSummaries(): List<ProjectSummary> =
        mongoTemplate.findAll(ProjectSummary::class.java)

    override fun getSessionSummaries(groupId: String, artifactId: String): List<SessionSummary> =
        mongoTemplate.find(
            Query().apply {
                addCriteria(
                    Criteria().andOperator(
                        Criteria.where("groupId").isEqualTo(groupId),
                        Criteria.where("artifactId").isEqualTo(artifactId)
                    )
                )
            },
            SessionSummary::class.java
        )

    override fun getSessionProfile(id: String): SessionProfile =
        mongoTemplate.findOne(
            Query().apply {
                addCriteria(
                    Criteria.where("id").isEqualTo(id)
                )
            },
            SessionProfile::class.java
        ) ?: throw Exception("SessionProfile not found $id")
}