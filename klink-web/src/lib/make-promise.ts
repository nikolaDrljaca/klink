export default function makePromise<T, Args extends any[]>(
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
