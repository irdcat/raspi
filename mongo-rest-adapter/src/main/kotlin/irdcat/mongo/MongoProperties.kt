package irdcat.mongo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.mongo")
data class MongoProperties(val url: String) {
    fun toConnectionString() = "mongodb://$url"
}
