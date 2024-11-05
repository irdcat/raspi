import { 
    useEffect, 
    useMemo, 
    useRef, 
    useState 
} from "react"

export type UseAsyncEffectResult = {
    result: any,
    error: any,
    isLoading: boolean
}

/**
 * Variant of useEffect hook that supports async callbacks
 * See: https://marmelab.com/blog/2023/01/11/use-async-effect-react.html
 */
export default function useAsyncEffect(
    mountCallback: () => Promise<any>,
    unmountCallback: () => Promise<any>,
    dependencies: any[] = []
): UseAsyncEffectResult {
    const isMounted = useRef(false);
    const [ isLoading, setIsLoading ] = useState(false);
    const [ error, setError ] = useState<unknown>(undefined);
    const [ result, setResult ] = useState<any>();

    useEffect(() => {
        isMounted.current = true;
        return () => {
            isMounted.current = false;
        }
    }, []);

    useEffect(() => {
        let ignore = false;
        let mountSucceded = false;

        (async () => {
            // wait for the initial cleanup in Strict mode
            // avoids double mutation
            await Promise.resolve();
            if (!isMounted.current || ignore) {
                return;
            }
            setIsLoading(true);
            try {
                const result = await mountCallback();
                mountSucceded = true;
                if (isMounted.current && !ignore) {
                    setError(undefined);
                    setResult(result);
                    setIsLoading(false);
                } else {
                    // Component was unmounted before the mount callback returned
                    // Cancel it
                    unmountCallback();
                }
            } catch (error) {
                if(!isMounted.current) {
                    return;
                }
                setError(error);
                setIsLoading(false);
            }
        })();

        return () => {
            ignore = true;
            if (mountSucceded) {
                unmountCallback()
                    .then(() => {
                        if(!isMounted.current) {
                            return;
                        }
                        setResult(undefined);
                    })
                    .catch((error: unknown) => {
                        if(!isMounted.current) {
                            return;
                        }
                        setError(error);
                    })
            }
        }
    }, dependencies); // eslint-disable-line react-hooks/exhaustive-deps

    return useMemo(() => ({ 
        result, error, isLoading 
    }), [result, error, isLoading ]);
}