import { useEffect, useState } from "react";

interface FetchState<T> {
    data: T | null,
    isLoading: boolean,
    error: Error | null
}

const useFetchData = <T> (url: string, options?: RequestInit): FetchState<T> => {
    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        let cancel = false;
        setIsLoading(true);
        fetch(url, options)
            .then(res => {
                if (!res.ok) {
                    const e = new Error(`${res.status} ${res.statusText}`);
                    e.name = "HTTP Error";
                    throw e;
                }
                return res.json() as T;
            })
            .then(body => {
                if (!cancel) {
                    setData(body)
                    setError(null)
                }
            })
            .catch(e => {
                if (!cancel) {
                    setData(null)
                    setError(e)
                }
            })
            .finally(() => {
                if (!cancel) {
                    setIsLoading(false)
                }
            })
        return () => {
            cancel = true;
        }
    }, [url, options]);

    return { data, isLoading, error }
}

export default useFetchData;
