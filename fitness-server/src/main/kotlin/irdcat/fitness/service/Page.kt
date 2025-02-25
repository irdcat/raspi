package irdcat.fitness.service

data class Page<T>(
    val content: List<T>,
    val currentPage: Long,
    val pageSize: Long,
    val totalResults: Long
)
