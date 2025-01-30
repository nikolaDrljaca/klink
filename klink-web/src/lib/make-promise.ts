import { ApiResponse } from "~/generated";

/**
 * Use to wrap async operation that could fail and you don't care what the error is.
 *
 * Wrapping generated api calls with this should be fine since those calls will 
 * return non 200 results as errors.
 *
 * Use `raw` variant from generated calls
 * @example
 * const [err, data] = await makeRequest(api.getFooRaw(args));
 */
export default async function makeRequest<T>(apiCall: Promise<ApiResponse<T>>): Promise<[Error | null, T | null]> {
    try {
        const valueRaw = await apiCall;
        const response = valueRaw.raw;

        if (!response.ok) {
            const errorText = await response.text().catch(() => "Unknown error");
            return [new Error(`HTTP ${response.status}: ${errorText}`), null];
        }

        if (response.status === 204) {
            return [null, null as T]; // Handle No Content (204)
        }

        const data: T = await response.json();
        return [null, data];
    } catch (error) {
        return [error instanceof Error ? error : new Error("Unknown error"), null];
    }
}
