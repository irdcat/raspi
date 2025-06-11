import { Box, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import useFetchData from "../hooks/useFetchData"
import EmptyState from "../components/EmptyState";
import LoadingState from "../components/LoadingState";
import { useNavigate } from "react-router-dom";

const DatabasesView = (props: {
    data: Array<DatabaseMetadata> | null, 
    isLoading: boolean, 
    error: Error | null
}) => {

    const { data, isLoading, error } = props;
    const navigate = useNavigate()

    if (isLoading) {
        return <LoadingState/>
    }

    if (error) {
        return <EmptyState title={error.name} message={error.message}/>
    }

    if (!data) {
        return <EmptyState title={"Unknown"} message={"Something went wrong!"}/>
    }

    return (
        <TableContainer>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell colSpan={4}>
                            <Typography fontWeight="semibold" fontSize={20}>
                                Databases
                            </Typography>
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Name</TableCell>
                        <TableCell>Size on disk</TableCell>
                        <TableCell>Empty</TableCell>
                        <TableCell/>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        data.map(metadata => (
                            <TableRow>
                                <TableCell>
                                    {metadata.name}
                                </TableCell>
                                <TableCell>
                                    {metadata.sizeOnDisk 
                                        ? `${metadata.sizeOnDisk} B` 
                                        : "Unavailable" }
                                </TableCell>
                                <TableCell>
                                    {metadata.empty !== null 
                                        ? (metadata.empty ? "Yes" : "No") 
                                        : "Unavailable" }
                                </TableCell>
                                <TableCell>
                                    <Box sx={{ display: 'flex', columnGap: '2px', justifyContent: 'flex-end' }}>
                                        <Button 
                                            size="small" 
                                            color="primary" 
                                            variant="outlined"
                                            onClick={() => navigate(`/${metadata.name}`)}>
                                            View
                                        </Button>
                                        <Button size="small" color="secondary" variant="outlined">Export</Button>
                                        <Button size="small" color="error" variant="outlined">Delete</Button>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))
                    }
                </TableBody>
            </Table>
        </TableContainer>
    )
}

const ServerStatusView = (props: {
    data: ServerStatus | null, 
    isLoading: boolean, 
    error: Error | null
}) => {

    const { data, isLoading, error } = props;

    if (isLoading) {
        return <LoadingState/>
    }

    if (error) {
        return <EmptyState title={error.name} message={error.message}/>
    }

    if (!data) {
        return <EmptyState title={"Unknown"} message={"Something went wrong!"}/>
    }

    const rows: [string, any][] = [
        ["Hostname", data.host],
        ["Version", data.version],
        ["Uptime", data.uptime],
        ["Server Time", data.localTime],
        ["Current connections", data.connections.current],
        ["Available connections", data.connections.available],
        ["Active clients", data.globalLock.activeClients.total],
        ["Queued operations", data.globalLock.currentQueue.total],
        ["Clients reading", data.globalLock.activeClients.readers],
        ["Clients writing", data.globalLock.activeClients.writers],
        ["Read Lock Queue", data.globalLock.currentQueue.readers],
        ["Write Lock Queue", data.globalLock.currentQueue.writers],
        ["Total inserts", data.opcounters.insert],
        ["Total queries", data.opcounters.query],
        ["Total updates", data.opcounters.update],
        ["Total deletes", data.opcounters.delete]
    ]

    return (
        <TableContainer>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell colSpan={2}>
                            <Typography fontWeight="semibold" fontSize={20}>
                                Server Status
                            </Typography>
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        rows.map(([key, value]) => (
                            <TableRow>
                                <TableCell>{key}</TableCell>
                                <TableCell align="right">{value.toString()}</TableCell>
                            </TableRow>
                        ))
                    }
                </TableBody>
            </Table>
        </TableContainer>
    )
}

const Home = () => {
    const serverStatusFetchData = useFetchData<ServerStatus>("/api/status");
    const databasesFetchData = useFetchData<Array<DatabaseMetadata>>("/api/database");
    
    return (
        <Box sx={{ width: '100%', height: '100%', display: 'flex', columnGap: '4px', padding: '10px' }}>
            <Box sx={{ flexGrow: 3 }}>
                <DatabasesView {...databasesFetchData}/>
            </Box>
            <Box sx={{ flexGrow: 1 }}>
                <ServerStatusView {...serverStatusFetchData}/>
            </Box>
        </Box>
    )
}

export default Home;