package irdcat.fitness.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

@Configuration
class MongoConfiguration {

    @Bean
    fun mongoCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(listOf(
            object: Converter<LocalDate, Date> {
                override fun convert(source: LocalDate): Date {
                    return Date(source
                        .atStartOfDay()
                        .atZone(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli())
                }
            },
            object: Converter<Date, LocalDate> {
                override fun convert(source: Date): LocalDate? {
                    return source.toInstant()
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                }
            }
        ))
    }
}