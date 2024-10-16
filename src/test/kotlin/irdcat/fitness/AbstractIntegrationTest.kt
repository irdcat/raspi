package irdcat.fitness

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainersConfiguration::class)
abstract class AbstractIntegrationTest