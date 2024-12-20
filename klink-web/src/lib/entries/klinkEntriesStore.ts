import { makePersisted, PersistenceSyncAPI, PersistenceSyncData, wsSync } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { KlinkEntry } from "~/generated/models";
import { Klink } from "~/lib/klinks/store";

// TODO: Introduce local type for KlinkEntry!!

type KlinkEntriesStore = {
    state: KlinkEntry[];
    onAddEntry: (url: string) => void;
    onRemoveEntry: (url: string) => void;
}

export function createKlinkEntriesStore(klink: Klink): KlinkEntriesStore {
    const klinkId = klink.id;
    const forageKey = `klink-items-${klinkId}`;

    const klinkItemsStore = createStore<Array<KlinkEntry>>([], { name: forageKey });
    const [state, setState] = makePersisted(
        klinkItemsStore,
        {
            name: forageKey,
            storage: localforage,
            sync: createKlinkSyncApi(
                klink,
                forageKey,
                // use created store directly for received messages to avoid feedback loop
                // with `setState`
                (value) => klinkItemsStore[1](JSON.parse(value)))
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

function createKlinkSyncApi(klink: Klink, forageKey: string, onMessage: (value: string) => void): PersistenceSyncAPI | undefined {
    // if keys are missing, collection is local, do NOT connect to socket
    if (!klink.readKey && !klink.writeKey) {
        return undefined;
    }
    // create socket path
    const path = buildSocketPath(klink);
    // connect to socket
    const socket = new WebSocket(path);
    // `wsSync` will push messages up, but will not pull them down
    // manually pull down using event listener
    socket.onmessage = (e: MessageEvent) => {
        const data: PersistenceSyncData = JSON.parse(e.data);
        // set local storage
        localforage.setItem(forageKey, data.newValue);
        // notify that a message was received
        onMessage(data.newValue);
    }
    return wsSync(socket, true);
}

function buildSocketPath(klink: Klink): string {
    const WS_PATH = import.meta.env.VITE_WS_PATH;
    const base = [`${WS_PATH}/klink/wsSync/${klink.id}?read_key=${klink.readKey}`]
    if (klink.writeKey) {
        base.push(`&write_key=${klink.writeKey}`);
    }
    return base.join("");
}
