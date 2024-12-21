import { useAppStore } from "~/lib/klinks/context";
import { createStore } from "solid-js/store";
import { CreateKlinkPayload, KlinkEntry } from "~/generated";
import localforage from "localforage";
import { createEventBus } from "@solid-primitives/event-bus";
import klinkApi from "~/lib/klinkApi/api";

type ShareKlinkEvent =
    | { type: "success" }
    | { type: "failure" }
    | { type: "readWrite", url: string }
    | { type: "readOnly", url: string }

export default function shareKlinkStore(klinkId: string) {
    const appBasePath = import.meta.env.VITE_APP_BASE;
    const store = useAppStore();
    const klink = store.state.klinks.find(it => it.id === klinkId)!;
    const api = klinkApi();

    const { listen, emit, clear } = createEventBus<ShareKlinkEvent>();

    const [klinkStore, setStore] = createStore({
        klink: klink,
        loading: false,
        get isShared() {
            return !!klink.readKey && !!klink.writeKey;
        },
    });

    return {
        klinkStore,
        listen,

        async shareKlink() {
            if (klinkStore.loading) {
                return;
            }
            const entriesRaw: string = await localforage.getItem(`klink-items-${klinkStore.klink.id}`);
            const entries: KlinkEntry[] = JSON.parse(entriesRaw ?? "[]");
            const payload: CreateKlinkPayload = {
                name: klinkStore.klink.name,
                id: klinkStore.klink.id,
                entries: entries,
                description: klinkStore.klink.description
            }
            try {
                setStore('loading', true);
                const response = await api.createKlink({ createKlinkPayload: payload });
                store.update(state => {
                    const curr = state.klinks.find(it => it.id === response.id);
                    if (curr) {
                        curr.readKey = response.readKey;
                        curr.writeKey = response.writeKey;
                    }
                });
                setStore('loading', false);
                emit({ type: "success" });
            } catch (e) {
                emit({ type: "failure" });
                setStore('loading', false);
            }
        },

        createShareLink() {
            const url = [`${appBasePath}/c/${klinkStore.klink.id}/i?read_key=${klinkStore.klink.readKey}`];
            if (klinkStore.klink.writeKey) {
                url.push(`&write_key=${klinkStore.klink.writeKey}`);
            }
            emit({
                type: 'readWrite',
                url: url.join("")
            });
        },

        createReadOnlyLink() {
            const url = [`${appBasePath}/c/${klinkStore.klink.id}/i?read_key=${klinkStore.klink.readKey}`];
            emit({
                type: 'readOnly',
                url: url.join("")
            });
        }
    }
}
