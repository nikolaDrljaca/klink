/**
 * Use to wrap async operation that could fail and you don't care what the error is.
 *
 * Wrapping generated api calls with this should be fine since those calls will 
 * return non 200 results as errors.
 */
export default function makeRequest<T, Args extends any[]>(
    fn: (...args: Args) => Promise<T>
): (...args: Args) => Promise<[Error | null, T | null]> {
    return async (...args: Args): Promise<[Error | null, T | null]> => {
        try {
            const result = await fn(...args);
            return [null, result];
        } catch (error) {
            return [error instanceof Error ? error : new Error(String(error)), null];
        }
    };
}
