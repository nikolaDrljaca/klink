import { useAppStore } from "~/lib/klinks/context";
import { createEventBus } from "@solid-primitives/event-bus";
import { createStore } from "solid-js/store";
import localforage from "localforage";
import klinkApi from "~/lib/klinkApi/api";

type DeleteKlinkEvent =
    | { type: "success" }
    | { type: "failure" }

export default function deleteKlinkStore(klinkId: string) {
    const store = useAppStore();
    const klink = store.state.klinks.find(it => it.id === klinkId);
    const api = klinkApi();

    const { listen, emit, clear } = createEventBus<DeleteKlinkEvent>();

    const [klinkStore, setStore] = createStore({
        klink: klink,
        loading: false,
        shouldDeleteShared: false,
        get isShared() {
            return !!klink.readKey && !!klink.writeKey;
        }
    });

    return {
        state: klinkStore,
        listen,

        async deleteKlink() {
            if (klinkStore.loading) {
                return;
            }
            const forageKey = `klink-items-${klinkId}`;
            // delete local entries
            await localforage.removeItem(forageKey);
            if (!klinkStore.shouldDeleteShared) {
                // only local delete
                emit({ type: 'success' });
                store.update(state => {
                    state.klinks = state.klinks.filter(it => it.id !== klinkId);
                    state.selectedKlinkId = null;
                });
                return;
            }
            // remote and local delete
            try {
                setStore('loading', true);
                // call remote delete
                await api.deleteKlink({
                    klinkId: klink.id,
                    readKey: klink.readKey,
                    writeKey: klink.writeKey
                });
                // on success local delete
                emit({ type: 'success' });
                setStore('loading', false);
                store.update(state => {
                    state.klinks = state.klinks.filter(it => it.id !== klinkId);
                    state.selectedKlinkId = null;
                });
            } catch (e) {
                console.error(e);
                setStore('loading', false);
                emit({ type: 'failure' });
            }
        },

        setShouldDeleteShared(value: boolean) {
            setStore('shouldDeleteShared', value);
        }
    }
}
