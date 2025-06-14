package irdcat.auth.security

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Repository

@Repository
class MongoRegisteredClientRepository(
    private val mongoTemplate: MongoTemplate
): RegisteredClientRepository {
    companion object {
        private const val COLLECTION_NAME = "registeredClientData"
    }

    override fun save(registeredClient: RegisteredClient?) {
        registeredClient?.let {
            mongoTemplate.insert(it, COLLECTION_NAME)
        }
    }

    override fun findById(id: String?): RegisteredClient? {
        return id?.let {
            val query = query(where("id").isEqualTo(it))
            mongoTemplate.findOne(query, RegisteredClient::class.java, COLLECTION_NAME)
        }
    }

    override fun findByClientId(clientId: String?): RegisteredClient? {
        return clientId?.let {
            val query = query(where("clientId").isEqualTo(it))
            mongoTemplate.findOne(query, RegisteredClient::class.java, COLLECTION_NAME)
        }
    }
}