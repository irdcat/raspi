package irdcat.mongo.status

import java.time.Instant
import java.time.LocalDate
import java.util.Date

data class ServerStatus(
    val asserts: AssertMetrics,
    val bucketCatalog: BucketCatalogMetrics,
    val catalogStats: CatalogStats,
    val changeStreamPreImages: ChangeStreamPreImages,
    val connections: ConnectionsMetrics,
    val defaultRWConcern: DefaultRWConcern,
    val electionMetrics: ElectionMetrics,
    val extraInfo: ExtraInfo,
    val flowControl: FlowControlMetrics,
    val globalLock: GlobalLockMetrics,
    val hedgingMetrics: HedgingMetrics,
    val indexBuilds: IndexBuildsMetrics,
    val indexBulkBuilder: IndexBulkBuilderMetrics,
    val indexStats: IndexStatsMetrics,
    val host: String,
    val advisoryHostFQDNs: List<String>,
    val version: String,
    val process: String,
    val service: String,
    val pid: Long,
    val uptime: Double,
    val uptimeMillis: Long,
    val uptimeEstimate: Long,
    val localTime: LocalDate,
    val locks: Map<LockType, Map<LockMode, Long>>,
    val logicalSessionRecordCacheMetrics: LogicalSessionRecordCacheMetrics,
    val mem: MemoryMetrics) {

    data class AssertMetrics(
        val regular: Double,
        val warning: Double,
        val msg: Double,
        val user: Double,
        val rollovers: Double)

    data class BucketCatalogMetrics(
        val numBuckets: Double,
        val numOpenBuckets: Double,
        val numIdleBuckets: Double,
        val memoryUsage: Double,
        val numBucketInserts: Double,
        val numBucketUpdates: Double,
        val numBucketsOpenedDueToMetadata: Double,
        val numBucketsClosedDueToCount: Double,
        val numBucketsClosedDueToSchemaChange: Double,
        val numBucketsClosedDueToSize: Double,
        val numBucketsClosedDueToTimeForward: Double,
        val numBucketsClosedDueToTimeBackward: Double,
        val numBucketsClosedDueToMemoryThreshold: Double,
        val numCommits: Double,
        val numMeasurementsGroupCommitted: Double,
        val numWaits: Double,
        val numMeasurementsCommitted: Double,
        val avgNumMeasurementsPerCommit: Double,
        val numBucketsClosedDueToReopening: Double,
        val numBucketsArchivedDueToMemoryThreshold: Double,
        val numBucketsArchivedDueToTimeBackward: Double,
        val numBucketsReopened: Double,
        val numBucketsKeptOpenDueToLargeMeasurements: Double,
        val numBucketsClosedDueToCachePressure: Double,
        val numBucketsFrozen: Double,
        val numCompressedBucketsConvertedToUnsorted: Double,
        val numBucketsFetched: Double,
        val numBucketsQueried: Double,
        val numBucketFetchesFailed: Double,
        val numBucketQueriesFailed: Double,
        val numBucketReopeningsFailed: Double,
        val numDuplicateBucketsReopened: Double,
        val stateManagement: StateManagement) {

        data class StateManagement(
            val bucketsManaged: Double,
            val currentEra: Double,
            val erasWithRemainingBuckets: Double,
            val trackedClearOperations: Double)
    }

    data class CatalogStats(
        val collections: Double,
        val capped: Double,
        val views: Double,
        val timeseries: Double,
        val internalCollections: Double,
        val internalViews: Double)

    data class ChangeStreamPreImages(
        val purgingJob: PurgingJob,
        val expireAfterSeconds: Double) {

        data class PurgingJob(
            val totalPass: Double,
            val docsDeleted: Double,
            val bytesDeleted: Double,
            val scannedCollections: Double,
            val scannedInternalCollections: Double,
            val maxStartWallTimeMillis: Double,
            val timeElapsedMillis: Double)
    }

    data class ConnectionsMetrics(
        val current: Double,
        val available: Double,
        val totalCreated : Double,
        val rejected: Double,
        val active: Double,
        val threaded: Double,
        val exhaustIsMaster: Double,
        val exhaustHello: Double,
        val awaitingTopologyChanges: Double,
        val loadBalanced: Double)
    
    data class DefaultRWConcern(
        val defaultReadConcern: DefaultReadConcern,
        val defaultWriteConcern: DefaultWriteConcern,
        val defaultWriteConcernSource: String,
        val defaultReadConcernSource: String,
        val updateOpTime: Instant,
        val updateWallClockTime: Date,
        val localUpdateWallClockTime: Date) {

        data class DefaultReadConcern(
            val level: String)
        
        data class DefaultWriteConcern(
            val w: String,
            val wtimeout: Integer,
            val j: Boolean)
    }

    data class ElectionMetrics(
        val stepUpCmd: CommandMetrics,
        val priorityTakeover: CommandMetrics,
        val catchUpTakeover: CommandMetrics,
        val electionTimeout: CommandMetrics,
        val freezeTimeout: CommandMetrics,
        val numStepDownsCausedByHigherTerm: Long,
        val numCatchUps: Long,
        val numCatchUpsSucceeded: Long,
        val numCatchUpsAlreadyCaughtUp: Long,
        val numCatchUpsSkipped: Long,
        val numCatchUpsTimedOut: Long,
        val numCatchUpsFailedWithError: Long,
        val numCatchUpsFailedWithNewTerm: Long,
        val numCatchUpsFailedWithReplSetAbortPrimaryCatchUpCmd: Long,
        val averageCatchUpOps: Double) {

        data class CommandMetrics(
            val called: Long,
            val successful: Long)
    }

    data class ExtraInfo(
        val note: String,
        val pageFaults: Double)

    data class FlowControlMetrics(
        val enabled: Boolean,
        val targetRateLimit: Integer,
        val timeAcquiringMicros: Long,
        val locksPerKiloOp: Double,
        val sustainerRate: Integer,
        val isLagged: Boolean,
        val isLaggedCount: Integer,
        val isLaggedTimeMicros: Long)

    data class GlobalLockMetrics(
        val totalTime: Long,
        val currentQueue: OperationMetrics,
        val activeClients: OperationMetrics) {

        data class OperationMetrics(
            val total: Double,
            val readers: Double,
            val writers: Double)
    }

    data class HedgingMetrics(
        val numTotalOperations: Double,
        val numTotalHedgedOperations: Double,
        val numAdvantageouslyHedgedOperations: Double)

    data class IndexBuildsMetrics(
        val total: Double,
        val killedDueToInsufficientDiskSpace: Double,
        val failedDueToDataCorruption: Double)

    data class IndexBulkBuilderMetrics(
        val count: Long,
        val resumed: Long,
        val filesOpenedForExternalSort: Long,
        val filesClosedForExternalSort: Long,
        val spilledRanges: Long,
        val bytesSpilledUncompressed: Long,
        val bytesSpilled: Long,
        val numSorted: Long,
        val bytesSorted: Long,
        val memUsage: Long)

    data class IndexStatsMetrics(
        val count: Long,
        val features: Map<String, IndexFeatureStats>) {

        data class IndexFeatureStats(
            val count: Long,
            val accesses: Long)
    }

    @Suppress("unused")
    enum class LockType {
        ParallelBatchWriterMode,
        ReplicationStateTransition,
        Global,
        Database,
        Collection,
        Mutex,
        Metadata,
        DDLDatabase,
        DDLCollection,
        oplog
    }

    @Suppress("unused")
    enum class LockMode(val value: String) {
        Shared("R"),
        Exclusive("W"),
        IntentShared("r"),
        IntentExclusive("w")
    }

    data class LogicalSessionRecordCacheMetrics(
        val activeSessionsCount: Double,
        val sessionsCollectionJobCount: Double,
        val lastSessionsCollectionJobDurationMillis: Double,
        val lastSessionsCollectionJobTimestamp: Instant,
        val lastSessionsCollectionJobEntriesRefreshed: Double,
        val lastSessionsCollectionJobEntriesEnded: Double,
        val lastSessionsCollectionJobCursorsClosed: Double,
        val transactionReaperJobCount: Double,
        val lastTransactionReaperJobDurationMillis: Double,
        val lastTransactionReaperJobTimestamp: Instant,
        val lastTransactionReaperJobEntriesCleanedUp: Double,
        val sessionCatalogSize: Double)

    data class MemoryMetrics(
        val bits: Integer,
        val resident: Integer,
        val virtual: Integer,
        val supported: Boolean
    )

    data class ServerMetrics(
        val abortExpiredTransactions: Passes,
        val apiVersions: Map<String, String>,
        val aggStageCounters: Map<String, Long>,
        val changeStreams: ChangeStreamsMetrics,
        val commands: Map<String, CommandMetrics>,
        val cursor: CursorMetrics,
        val document: DocumentMetrics,
        val dotsAndDollarsFields: DotsAndDollarsFieldsMetrics,
        val getLastError: GetLastErrorMetrics,
        val mongos: Mongos,
        val networkMetrics: NetworkMetrics
    ) {

        data class Passes(val passes: Integer)

        data class ChangeStreamsMetrics(
            val largeEventsFailed: Long,
            val largeEventsSplit: Long,
            val showExpandedEvents: Long)

        data class CommandMetrics(
            val failed: Long,
            val validator: ValidatorMetrics,
            val total: Long,
            val rejected: Long) {

            data class ValidatorMetrics(
                val total: Long,
                val failed: Long,
                val jsonSchema: Long)
        }

        data class CursorMetrics(
            val moreThanOneBatch: Long,
            val timedOut: Long,
            val totalOpened: Long,
            val lifespan: Lifespan,
            val open: Open) {

            data class Lifespan(
                val greaterThanOrEqual10Minutes: Long,
                val lessThan10Minutes: Long,
                val lessThan15Seconds: Long,
                val lessThan1Minute: Long,
                val lessThan1Second: Long,
                val lessThan30Seconds: Long,
                val lessThan5Seconds: Long)

            data class Open(
                val noTimeout: Long,
                val pinned: Long,
                val multiTarget: Long,
                val singleTarget: Long,
                val total: Long)
        }

        data class DocumentMetrics(
            val deleted: Long,
            val inserted: Long,
            val returned: Long,
            val updated: Long)

        data class DotsAndDollarsFieldsMetrics(
            val inserts: Long,
            val updates: Long)

        data class GetLastErrorMetrics(
            val wtime: WTime,
            val wtimeouts: Long,
            val default: Default) {

            data class WTime(val num: Double, val totalMillis: Double)
            data class Default(val unsatifiable: Long, val wtimeouts: Long)
        }

        data class Mongos(val cursor: Cursor) {
            data class Cursor(val moreThanOneBatch: Long, val totalOpened: Long)
        }

        data class NetworkMetrics(
            val totalEgressConnectionEstablishmentTimeMillis: Long,
            val totalIngressTLSConnections: Long,
            val totalIngressTLSHandshakeTimeMillis: Long,
            val totalTimeForEgressConnectionAcquiredToWireMicros: Long,
            val totalTimeToFirstNonAuthCommandMillis: Long)

        data class OperationMetrics(
            val killedDueToClientDisconnect: Long,
            val killedDueToDefaultMaxTimeMSExpired: Long,
            val killedDueToMaxTimeMSExpired: Long,
            val numConnectionNetworkTimeouts: Long,
            val scanAndOrder: Long,
            val totalTimeWaitingBeforeConnectionTimeoutMillis: Long,
            val unsendableCompletedResponses: Long,
            val writeConflicts: Long)
    }
}
