package irdcat.fitness

import irdcat.fitness.model.Exercise
import irdcat.fitness.model.Training
import irdcat.fitness.model.TrainingExercise
import irdcat.fitness.model.TrainingExerciseSet
import irdcat.fitness.repository.ExerciseRepository
import irdcat.fitness.repository.TrainingRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.yaml.snakeyaml.Yaml
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.collections.LinkedHashMap

@Profile("!test")
@Configuration
class LocalApplicationInitializer(
    private val exerciseRepository: ExerciseRepository,
    private val trainingRepository: TrainingRepository
): InitializingBean {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(LocalApplicationInitializer::class.java)

        @JvmStatic
        private val yaml = Yaml()

        @JvmStatic
        private val dateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC))

        private const val TEST_EXERCISES_FILE = "test-exercises.yaml"
        private const val TEST_TRAININGS_FILE = "test-trainings.yaml"
    }

    override fun afterPropertiesSet() {
        Mono.just(TEST_EXERCISES_FILE)
            .map { this.javaClass.classLoader.getResourceAsStream(it) }
            .map { yaml.load<List<LinkedHashMap<String, Any>>>(it) }
            .flatMapMany { Flux.fromIterable(it!!) }
            .map {
                Exercise(
                    it["id"]!!.toString(),
                    it["name"]!!.toString(),
                    it["isBodyWeight"]!!.toString().toBoolean()
                )
            }
            .collectList()
            .flatMapMany { exerciseRepository.insert(it) }
            .subscribe { logger.info("Added exercise {}", it) }

        Mono.just(TEST_TRAININGS_FILE)
            .map { this.javaClass.classLoader.getResourceAsStream(it) }
            .map { yaml.load<List<LinkedHashMap<String, Any>>>(it) }
            .flatMapMany { Flux.fromIterable(it) }
            .map {
                Training(
                    it["id"]!!.toString(),
                    null,
                    LocalDate.parse(it["date"]!!.toString(), dateTimeFormatter),
                    it["bodyWeight"]!!.toString().toFloat(),
                    (it["exercises"] as List<LinkedHashMap<String, Any>>)
                        .map { e ->
                            TrainingExercise(
                                e["order"].toString().toInt(),
                                e["exerciseId"].toString(),
                                (e["sets"] as List<LinkedHashMap<String, Any>>)
                                    .map { s ->
                                        TrainingExerciseSet(
                                            s["reps"]!!.toString().toInt(),
                                            s["weight"]!!.toString().toFloat()
                                        )
                                    }
                            )
                        }
                )
            }
            .map {
                val now = LocalDate.now()
                val monthValueResult = now.monthValue - 3 + it.date.monthValue
                val monthValue = if (monthValueResult <= 0) {
                    12 + monthValueResult
                } else {
                    monthValueResult
                }
                val year = if (monthValueResult <= 0) {
                    now.year - 1
                } else {
                    now.year
                }
                val updatedDate = LocalDate.of(
                    year,
                    monthValue,
                    if (it.date.dayOfMonth > 30) { 30 } else { it.date.dayOfMonth })
                Training(
                    it.id,
                    null,
                    updatedDate,
                    it.bodyWeight,
                    it.exercises)
            }
            .collectList()
            .flatMapMany { trainingRepository.insert(it) }
            .subscribe { logger.info("Added training {}", it) }
    }
}