import { makePersisted } from "@solid-primitives/storage"
import localforage from "localforage"
import { createStore } from "solid-js/store"
import toast from "solid-toast"
import { CreateKlinkRequest, KlinkApi } from "~/generated"

export type KlinkCollectionStore = {
    klinks: Array<Klink>,
    selectedKlinkId: string | null
}

export type KlinkCollectionActions = {
    createKlink: (payload: {
        name: string,
        description?: string,
    }) => void,

    shareKlink: (klinkId: string) => void,
    //
    // TODO: move to KlinkStore (wsSync)
    // witholdKlink: (klinkId: string) => void,

    importKlink: (payload: {
        klinkId: string,
        readKey: string,
        writeKey?: string
    }) => void,

    deleteKlink: (klinkId: string) => void,

    selectKlink: (klinkId: string) => void,

    copyKlink: (klinkId: string) => void
}

export type Klink = {
    id: string,
    name: string,
    description: string | null,
    readKey: string | null,
    writeKey: string | null,
}

export function createAppStore() {
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
    const api = new KlinkApi();

    const actions: KlinkCollectionActions = {
        createKlink: function(payload: {
            name: string
            description?: string
        }): void {
            const klink: Klink = {
                id: crypto.randomUUID(),
                name: payload.name,
                description: payload.description,
                readKey: null,
                writeKey: null
            }
            setState('klinks', (currentKlinks) => [klink, ...currentKlinks])
            toast.success(`${klink.name} created!`);
        },

        importKlink: function(payload: {
            klinkId: string
            readKey: string
            writeKey?: string
        }): void {
            throw new Error("Function not implemented.")
        },

        deleteKlink: function(klinkId: string): void {
            setState(
                'klinks',
                (currentKlinks) => currentKlinks.filter(it => it.id !== klinkId)
            )
        },

        selectKlink: function(klinkId: string): void {
            setState('selectedKlinkId', klinkId)
        },

        shareKlink: function(klinkId: string): void {
            const current = state.klinks.find(it => it.id === klinkId)
            if (!current) {
                return;
            }
            const params: CreateKlinkRequest = {
                createKlinkPayload: {
                    name: current.name,
                    id: current.id,
                    entries: []
                }
            }
            api.createKlink(params)
                .then((response) => {
                    setState(
                        'klinks',
                        (klinks: Klink[]) => klinks.map(it => it.id === response.id ? { ...it, readKey: response.readKey, writeKey: response.writeKey } : it)
                    );
                    toast.success("Klink shared!");
                })
                .catch(() => toast.error("Something went wrong."));
        },

        copyKlink: function(klinkId: string): void {
            const current = state.klinks.find(it => it.id === klinkId);
            if (!current) {
                return;
            }
            const updated: Klink = {
                ...current,
                name: `Copy of ${current.name}`,
                id: crypto.randomUUID(),
                readKey: null,
                writeKey: null
            }
            setState('klinks', it => [updated, ...it]);
        }
    }

    return {
        state,
        actions
    }
}
