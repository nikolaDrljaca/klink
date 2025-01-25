import { createStore } from "solid-js/store";
import { CreateKlinkPayload, KlinkEntry } from "~/generated";
import localforage from "localforage";
import { createEventBus } from "@solid-primitives/event-bus";
import { useAppStore } from "~/stores/app-store-context";
import makeKlinkApi from "~/lib/make-klink-api";
import makeRelativeTime from "~/lib/relative-time";

type ShareKlinkEvent =
    | { type: "success" }
    | { type: "failure" }

export default function shareKlinkStore(klinkId: string) {
    const appBasePath = import.meta.env.VITE_APP_BASE;
    const store = useAppStore();
    const klink = store.state.klinks.find(it => it.id === klinkId)!;
    const api = makeKlinkApi();
    const relativeTime = makeRelativeTime();

    const { listen, emit, clear } = createEventBus<ShareKlinkEvent>();

    const [klinkStore, setStore] = createStore({
        klink: klink,
        loading: false,
        readOnlyChecked: false,
        get isShared() {
            return !!klink.readKey;
        },
        get isReadOnly() {
            if (!!klink.writeKey) {
                return false;
            }
            return !!klink.readKey;
        },
        get shareLink() {
            if (klinkStore.readOnlyChecked) {
                return createReadOnlyLink();
            }
            return createShareLink();
        },
        get socialShareTarget() {
            return {
                title: klinkStore.klink.name,
                description: klinkStore.klink.description ?? "",
                url: klinkStore.shareLink
            }
        }
    });

    const createShareLink = () => {
        const url = [`${appBasePath}/c/${klinkStore.klink.id}/i?read_key=${klinkStore.klink.readKey}`];
        if (klinkStore.klink.writeKey) {
            url.push(`&write_key=${klinkStore.klink.writeKey}`);
        }
        return url.join("");
    }

    const createReadOnlyLink = () => {
        const url = [`${appBasePath}/c/${klinkStore.klink.id}/i?read_key=${klinkStore.klink.readKey}`];
        return url.join("");
    }

    return {
        klinkStore,
        listen,

        setReadOnlyChecked() {
            setStore('readOnlyChecked', !klinkStore.readOnlyChecked)
        },

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
                        curr.updatedAt = relativeTime.unixFromResponse(response.updatedAt)
                    }
                });
                setStore('loading', false);
                emit({ type: "success" });
            } catch (e) {
                emit({ type: "failure" });
                setStore('loading', false);
            }
        },
    }
}
