import { createEffect, onCleanup } from "solid-js";

type KlinkEventStreamOptions = {
  url: string;
  onMessage: (event: MessageEvent, close: () => void) => void;
  maxRetries?: number;
  retryDelayMs?: number;
};

export default function createKlinkEventStream({
  url,
  onMessage,
  maxRetries = 5,
  retryDelayMs = 4000,
}: KlinkEventStreamOptions) {
  createEffect(() => {
    let ws: WebSocket | null = null;
    let reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
    let retries = 0;
    let manuallyClosed = false;

    const close = () => {
      manuallyClosed = true;
      if (reconnectTimeout) clearTimeout(reconnectTimeout);
      ws?.close();
    };

    const connect = () => {
      ws = new WebSocket(url);

      ws.onmessage = (event) => {
        onMessage(event, close);
      };

      ws.onclose = () => {
        if (!manuallyClosed && retries < maxRetries) {
          retries++;
          reconnectTimeout = setTimeout(connect, retryDelayMs);
        }
      };

      ws.onerror = () => {
        ws?.close(); // triggers onclose
      };
    };

    connect();

    onCleanup(() => {
      close(); // ensures proper teardown
    });
  });
}
