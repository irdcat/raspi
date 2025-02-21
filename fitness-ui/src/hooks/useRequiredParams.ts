import { useParams } from "react-router-dom"

type RequiredParams<Key extends string = string> = {
    readonly [key in Key]: string;
}

const useRequiredParams = <Params extends string>(
    requiredParamNames: Params[]
): RequiredParams<typeof requiredParamNames[number]> => {

    const routeParams = useParams();
    for (const paramName of requiredParamNames) {
        const parameter = routeParams[paramName];
        if (!parameter) {
            throw new Error(`Parameter with name '${paramName}' is required by this component!`);
        }
    }

    return routeParams as RequiredParams<typeof requiredParamNames[number]>;
}

export default useRequiredParams;