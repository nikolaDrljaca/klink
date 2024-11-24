import { makePersisted } from "@solid-primitives/storage"
import localforage from "localforage"
import { createStore } from "solid-js/store"
import { KlinkApi } from "~/generated"

export type DashboardStore = {
    klinks: Array<Klink>,
    selectedKlinkId: string | null
}

export type DashboardActions = {
    createKlink: (payload: {
        name: string,
        description?: string,
    }) => void,

    // TODO: move to KlinkStore (wsSync)
    // shareKlink: (klinkId: string) => void,
    //
    // TODO: move to KlinkStore (wsSync)
    // witholdKlink: (klinkId: string) => void,

    importKlink: (payload: {
        klinkId: string,
        readKey: string,
        writeKey?: string
    }) => void,

    deleteKlink: (klinkId: string) => void
}

export type Klink = {
    id: string,
    name: string,
    description: string | null,
    readKey: string | null,
    writeKey: string | null,
}

export function createAppStore() {
    const store = createStore<DashboardStore>({
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

    const actions: DashboardActions = {
        createKlink: function(payload: {
            name: string;
            description?: string;
        }): void {
            const klink: Klink = {
                id: crypto.randomUUID(),
                name: payload.name,
                description: payload.description,
                readKey: null,
                writeKey: null
            }
            setState('klinks', (currentKlinks) => [klink, ...currentKlinks]);
        },

        importKlink: function(payload: {
            klinkId: string;
            readKey: string;
            writeKey?: string;
        }): void {
            throw new Error("Function not implemented.");
        },

        deleteKlink: function(klinkId: string): void {
            setState(
                'klinks',
                (currentKlinks) => currentKlinks.filter(it => it.id !== klinkId)
            );
        }
    }

    return {
        state,
        actions
    }
}
