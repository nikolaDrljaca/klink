import { ResponseError } from "~/generated";

export type AsyncResult<T> = [Error | null, T | null];

/*
 * Use to wrap calls made by KlinkApi.
 * const [err, data] = await makeRequest(api.getFoo(args));
 */
export default async function makeAsync<T>(
  call: () => Promise<T>,
): Promise<AsyncResult<T>> {
  try {
    const response = await call();
    return [null, response];
  } catch (e) {
    if (e instanceof ResponseError) {
      return [e, null];
    }
    return [
      e instanceof Error ? e : new Error("Unknown error during fetch"),
      null,
    ];
  }
}
