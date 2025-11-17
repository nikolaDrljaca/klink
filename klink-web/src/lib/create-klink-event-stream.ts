import { createEffect, onCleanup } from "solid-js";
import { KlinkModel } from "~/types/domain";

type KlinkEventStreamOptions = {
  klink: KlinkModel;
  onMessage: (event: MessageEvent, close: () => void) => void;
  maxRetries?: number;
  retryDelayMs?: number;
};

export default function createKlinkEventStream({
  klink,
  onMessage,
  maxRetries = 5,
  retryDelayMs = 4000,
}: KlinkEventStreamOptions) {
  createEffect(() => {
    const url = klinkEventPath(klink);

    if (!klink.isShared) {
      return;
    }

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

function klinkEventPath(data: { id: string; readKey: string }): string {
  const API_PATH = import.meta.env.VITE_APP_WS;
  return `${API_PATH}/events/klink/${data.id}?read_key=${data.readKey}`;
}
