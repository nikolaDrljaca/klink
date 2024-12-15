import { makePersisted } from "@solid-primitives/storage"
import localforage from "localforage"
import { createStore, produce } from "solid-js/store"

export type KlinkCollectionStore = {
    klinks: Array<Klink>,
    selectedKlinkId: string | null
}

export type Klink = {
    id: string,
    name: string,
    description: string | null,
    readKey: string | null,
    writeKey: string | null,
}

export type AppStore = {
    state: KlinkCollectionStore,
    update: (fn: (state: KlinkCollectionStore) => void) => void
}

export function createAppStore(): AppStore {
    const store = createStore<KlinkCollectionStore>({
        klinks: [],
        selectedKlinkId: null
    });
    const [state, setState] = makePersisted(
        store,
        {
            name: "dashboard-store",
            storage: localforage
        }
    );

    const update = (fn: (state: KlinkCollectionStore) => void) => {
        setState(produce(fn));
    }

    return {
        state,
        update
    }
}
