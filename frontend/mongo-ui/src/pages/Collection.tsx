import { useParams } from "react-router-dom"
import useFetchData from "../hooks/useFetchData";
import { Box, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import LoadingState from "../components/LoadingState";
import EmptyState from "../components/EmptyState";

const DocumentsView = (props: {
    data: Array<any> | null,
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
        return <EmptyState title="Unknown" message="Something went wrong!"/>
    }

    const dataArray = Array.of(...data)

    const allKeys = dataArray
        .flatMap(doc => Object.keys(doc))
        .filter((key, index, keyArray) => keyArray.indexOf(key) === index)

    return (
        <TableContainer>
            <Table>
                <TableHead>
                    <TableRow>
                        {allKeys.map(k => (
                            <TableCell>{k}</TableCell>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {dataArray.map(doc => (
                        <TableRow>
                            {allKeys.map(key => (
                                <TableCell>
                                    {typeof doc[key] === 'object' ? "Object" : doc[key]}
                                </TableCell>
                            ))}
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    )
}

const Collection = () => {
    const { db, col } = useParams();
    const documentsFetchData = useFetchData<Array<any>>(`/v2/mongo/api/database/${db}/collection/${col}/document`)

    return (
        <Box sx={{ width: '100%', height: '100%', padding: '10px' }}>
            <Typography variant="h4">
                {col}
            </Typography>
            <DocumentsView {...documentsFetchData}/>
        </Box>
    )
}

export default Collection;