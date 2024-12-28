import { createSignal } from "solid-js";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import makeKlinkApi from "~/lib/make-klink-api";
import { useAppStore } from "~/stores/app-store-context";
import { Klink } from "~/types/domain";

export default function collectionStore() {
    const { state, update } = useAppStore();
    const pathKlinkId = useKlinkIdParam();
    const api = makeKlinkApi();

    const [loading, setLoading] = createSignal(false);

    const reloadKlinkData = async () => {
        setLoading(true);
        for (const klink of state.klinks) {
            if (!klink.readKey) {
                continue;
            }
            try {
                const updated = await api.getKlink({
                    klinkId: klink.id,
                    readKey: klink.readKey
                });
                update(current => {
                    const found = current.klinks.find(it => it.id === klink.id)!;
                    found.name = updated.name;
                    found.description = updated.description;
                });
            } catch (e) {
                // NOTE: Swallow error.
            }
        }
        setLoading(false);
    }

    const copyKlink = (id: string) => {
        update(state => {
            const temp = state.klinks.find(it => it.id === id);
            if (!temp) {
                return;
            }
            const copy: Klink = {
                id: crypto.randomUUID(),
                name: `Copy of ${temp.name}`,
                description: "",
                updatedAt: Date.now(),
                readKey: null,
                writeKey: null
            }
            state.klinks.unshift(copy);
        });
    }

    const selectKlink = (id: string) => {
        update(state => {
            state.selectedKlinkId = id;
        });
    }

    const createKlink = (data: {
        name: string;
        description?: string;
    }) => {
        const klink: Klink = {
            id: crypto.randomUUID(),
            name: data.name,
            description: data.description,
            updatedAt: Date.now(),
            readKey: null,
            writeKey: null
        }
        update(state => {
            state.klinks.unshift(klink);
        });
    }

    return {
        state,
        reloadInProgress: loading,
        pathKlinkId,
        createKlink,
        selectKlink,
        copyKlink,
        reloadKlinkData
    }
}
