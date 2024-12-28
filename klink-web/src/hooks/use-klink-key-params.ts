import { useSearchParams } from "@solidjs/router";

export default function useKlinkKeyParams(): { readKey: string | null, writeKey: string | null } {
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

    return {
        readKey: extract(searchParams.read_key),
        writeKey: extract(searchParams.write_key)
    }
}
