import { makePersisted, PersistenceSyncData, wsSync } from "@solid-primitives/storage";
import localforage from "localforage";
import { Accessor, Component, For } from "solid-js";
import { createStore } from "solid-js/store";
import KlinkEntryListItem from "~/components/UrlListItem";
import { KlinkEntry } from "~/generated";
import { Klink } from "~/lib/klinks/store";

const WS_PATH = import.meta.env.VITE_WS_PATH;

type KlinkDetailsProps = {
  klink: Accessor<Klink>
}

const KlinkDetails: Component<KlinkDetailsProps> = (props) => {
  const klinkId = props.klink().id;
  const forageKey = `klink-items-${klinkId}`;

  const klinkItemsStore = createStore<Array<KlinkEntry>>([], { name: forageKey });
  // TODO: will need to pass keys -> fetch whole klink using params
  const socket = new WebSocket(`${WS_PATH}/klink/wsSync/${klinkId}`);
  socket.onmessage = (e: MessageEvent) => {
    const data: PersistenceSyncData = JSON.parse(e.data);
    localforage.setItem(forageKey, data.newValue);
  }
  const [state, setState] = makePersisted(
    klinkItemsStore,
    {
      name: forageKey,
      storage: localforage,
      sync: wsSync(socket, true)
    }
  );
  const onAddUrlItem = (url: string) => {
    const entry: KlinkEntry = {
      value: url
    }
    setState((val) => [...val, entry]);
  }
  const onRemoveUrlItem = (url: string) => {
    setState((val) => val.filter(it => it.value !== url));
  }

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">

      <p class="text-2xl px-4 pt-4 pb-2"># {props.klink().name}</p>

      {/* Button Row */}
      <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center w-full">
        <button class="btn btn-sm w-1/2" onClick={() => onAddUrlItem("https://vercel.com")}>Add New</button>
        <button class="btn btn-sm w-1/2" onClick={() => onRemoveUrlItem("https://vercel.com")}>Remove first</button>
      </div>

      {/* Link List - Container */}
      <div class="container items-center w-full px-4 space-y-2">
        {/* List Item */}
        <For each={state}>
          {(item,) =>
            <KlinkEntryListItem
              entry={item}
            />
          }
        </For>
      </div>
    </div>
  );
}

export default KlinkDetails;
