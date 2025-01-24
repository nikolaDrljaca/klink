/**
 * Use to wrap async operation that could fail and you don't care what the error is.
 *
 * Wrapping generated api calls with this should be fine since those calls will 
 * return non 200 results as errors.
 *
 * @example
 * const [err, data] = await makeRequest(() => api.getFoo(args));
 */
export default async function makeRequest<T>(fn: () => Promise<T>): Promise<[Error | null, T | null]> {
    try {
        const out = await fn();
        return [null, out];
    } catch (error) {
        return [error instanceof Error ? error : new Error(String(error)), null];
    }
}
