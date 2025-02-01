import { useSearchParams } from "@solidjs/router";

/**
 * Extract the `q` query parameter for the import route.
 */
export default function useKlinkImportParams(): string | null {
    const [searchParams, _] = useSearchParams();

    const extract = (value: string | string[]): string | null => {
        if (Array.isArray(value)) {
            return null;
        }
        if (value === "") {
            return null;
        }
        return value as string;
    }

    return extract(searchParams.q);
}
