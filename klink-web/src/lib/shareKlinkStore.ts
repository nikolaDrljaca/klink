import { useKlinkCollectionStore } from "~/lib/klinks/context";
import { createStore } from "solid-js/store";
import { CreateKlinkPayload, KlinkApi, KlinkEntry } from "~/generated";
import localforage from "localforage";
import { Klink } from "~/lib/klinks/store";
import { createEventBus } from "@solid-primitives/event-bus";

type ShareKlinkEvent =
    | { type: "success" }
    | { type: "failure" }
    | { type: "readWrite", url: string }
    | { type: "readOnly", url: string }

export default function shareKlinkStore(klinkId: string) {
    const klinkCollectionStore = useKlinkCollectionStore();
    const klink = klinkCollectionStore.klinks.find(it => it.id === klinkId)!;
    const api = new KlinkApi();
    // TODO: expose event bus where UI component will receive events to show toasts

    const { listen, emit, clear } = createEventBus<ShareKlinkEvent>();

    const [klinkStore, setStore] = createStore({
        klink: klink,
        get isShared() {
            return !!klink.readKey && !!klink.writeKey;
        }
    });

    return {
        klinkStore,
        listen,

        async shareKlink() {
            const entriesRaw: string = await localforage.getItem(`klink-items-${klinkStore.klink.id}`);
            const entries: KlinkEntry[] = JSON.parse(entriesRaw);
            const payload: CreateKlinkPayload = {
                name: klinkStore.klink.name,
                id: klinkStore.klink.id,
                entries: entries
            }
            try {
                const response = await api.createKlink({ createKlinkPayload: payload });
                setStore(
                    'klink',
                    (curr: Klink) => ({ ...curr, writeKey: response.writeKey, readKey: response.readKey })
                );
                emit({ type: "success" });
            } catch (e) {
                emit({ type: "failure" });
            }
        },

        createShareLink() {

        },

        createReadOnlyLink() {

        }
    }
}
