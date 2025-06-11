interface ServerConnections {
    current: number,
    available: number
}

interface ClientMetrics {
    total: number,
    readers: number,
    writers: number
}

interface GlobalLock {
    activeClients: ClientMetrics,
    currentQueue: ClientMetrics
}

interface OperationCounters {
    insert: number,
    query: number,
    update: number,
    delete: number
}

interface ServerStatus {
    host: string,
    version: string,
    uptime: number,
    localTime: Date,
    connections: ServerConnections,
    globalLock: GlobalLock,
    opcounters: OperationCounters
}

interface DatabaseMetadata {
    name: string,
    sizeOnDisk: number | null | undefined,
    empty: boolean | null | undefined
}

interface CollectionInfo {
    readOnly: boolean,
    uuid: string
}

interface CollectionIndex {
    v: number,
    key: [string, any][],
    name: string
}

interface CollectionMetadata {
    name: string,
    type: string,
    options: [string, any][],
    info: CollectionInfo,
    idIndex: CollectionIndex    
}

interface GenericApiError {
    status: number,
    message: string
}

interface DataApiError {
    databaseName: string,
    collectionName: string | null | undefined,
    documentId: string | null | undefined,
    status: number,
    error: string
}