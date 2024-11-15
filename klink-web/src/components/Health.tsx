import { Component, createEffect, createResource, createSignal, Match, onCleanup, Show, Switch } from "solid-js";

export const API_PATH = import.meta.env.VITE_API_PATH;
export const WS_PATH = import.meta.env.VITE_WS_PATH;
export const APP_LINK_BASE = import.meta.env.VITE_APP_BASE;

const restHealth = async () => {
  const response = await fetch(`${API_PATH}/health`);
  return response.ok;
}

function healthSocket() {
  const [isConnected, setIsConnected] = createSignal(false);

  let socket: WebSocket;

  const connect = () => {
    socket = new WebSocket(`${WS_PATH}/health`)

    socket.onopen = () => {
      setIsConnected(true);
    }

    socket.onerror = () => {
      setIsConnected(false);
    }

    socket.onclose = () => {
      setIsConnected(false);
    }
  }

  const disconnect = () => {
    if (socket) {
      socket.close();
    }
  }

  return { isConnected, connect, disconnect }
}

const Health: Component = () => {
  const [data] = createResource(restHealth)
  const socket = healthSocket()

  createEffect(() => {
    socket.connect();
  });

  onCleanup(() => {
    socket.disconnect();
  });

  return (
    <div class="flex flex-col">
      <div>
        <Show when={data.loading}>
          <p>Accessing REST service....</p>
        </Show>
        <Switch fallback={<>
          <p class="text-error">Failed to access REST service!</p>
        </>}>
          <Match when={data()}>
            <p class="text-green-400">REST service available!</p>
          </Match>
        </Switch>
      </div>

      <div>
        <Show when={!socket.isConnected()}>
          <p class="text-error">REALTIME service is unavailable.</p>
        </Show>
        <Show when={socket.isConnected()}>
          <p class="text-green-400">REALTIME service is available!</p>
        </Show>
      </div>
    </div>
  )
}

export default Health;
