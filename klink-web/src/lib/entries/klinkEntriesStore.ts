import { makePersisted, PersistenceSyncData, wsSync } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { KlinkEntry } from "~/generated/models";
import { Klink } from "~/lib/klinks/store";

type KlinkEntriesStore = {
    state: KlinkEntry[];
    onAddEntry: (url: string) => void;
    onRemoveEntry: (url: string) => void;
}

export function createKlinkEntriesStore(klink: Klink): KlinkEntriesStore {
    const klinkId = klink.id;
    const forageKey = `klink-items-${klinkId}`;

    const WS_PATH = import.meta.env.VITE_WS_PATH;

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
    const onAddEntry = (url: string) => {
        const entry: KlinkEntry = {
            value: url
        }
        setState((val: KlinkEntry[]) => {
            // Prevent adding duplicates
            const exists = val.find(it => it.value === url);
            if (exists) {
                return;
            }
            return [entry, ...val];
        });
    }
    const onRemoveEntry = (url: string) => {
        setState((val) => val.filter(it => it.value !== url));
    }

    return {
        state,
        onAddEntry,
        onRemoveEntry
    }
}
