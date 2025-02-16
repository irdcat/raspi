class StringUtils {
    
    static camelCaseToSpaced(camelCase: string): string {
        const spacedStr = camelCase.replace(/([A-Z])/g, ' $1').trim();
        const capitalizedStr = spacedStr.replace(/\b\w/g, (char) => char.toUpperCase());
        return capitalizedStr;
    }
}

export default StringUtils;