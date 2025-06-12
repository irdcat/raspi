import { useNavigate, useParams } from "react-router-dom"
import useFetchData from "../hooks/useFetchData";
import { Box, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import LoadingState from "../components/LoadingState";
import EmptyState from "../components/EmptyState";

const CollectionsView = (props: {
    data: Array<CollectionMetadata> | null,
    isLoading: boolean,
    error: Error | null
}) => {
    const { data, isLoading, error } = props;
    const navigate = useNavigate();

    if (isLoading) {
        return <LoadingState/>
    }

    if (error) {
        return <EmptyState title={error.name} message={error.message}/>
    }

    if (!data) {
        return <EmptyState title="Unknown" message="Something went wrong!"/>
    }

    return (
        <TableContainer>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Name</TableCell>
                        <TableCell>Type</TableCell>
                        <TableCell>Read Only</TableCell>
                        <TableCell>Uuid</TableCell>
                        <TableCell></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        data.map(collection => (
                            <TableRow>
                                <TableCell>{collection.name}</TableCell>
                                <TableCell>{collection.type}</TableCell>
                                <TableCell>{collection.info.readOnly ? "Yes" : "No"}</TableCell>
                                <TableCell>{collection.info.uuid}</TableCell>
                                <TableCell>
                                    <Box sx={{ display: 'flex', columnGap: '2px', justifyContent: 'flex-end' }}>
                                        <Button 
                                            size="small" 
                                            color="primary" 
                                            variant="outlined"
                                            onClick={() => navigate(collection.name)}>
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

const Database = () => {
    const { db } = useParams();
    const collectionsFetchData = useFetchData<Array<CollectionMetadata>>(`/v2/mongo/api/database/${db}/collection`)

    return (
        <Box sx={{ width: '100%', height: '100%', padding: '10px' }}>
            <Typography variant="h4">
                {db}
            </Typography>
            <CollectionsView {...collectionsFetchData}/>
        </Box>
    )
}

export default Database;