import { createSignal } from "solid-js";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import makeKlinkApi from "~/lib/make-klink-api";
import makeRequest from "~/lib/make-promise";
import makeRelativeTime from "~/lib/relative-time";
import { useAppStore } from "~/stores/app-store-context";
import { Klink } from "~/types/domain";

export default function collectionStore() {
    const { state, update } = useAppStore();
    const pathKlinkId = useKlinkIdParam();

    const api = makeKlinkApi();
    const time = makeRelativeTime();

    const [loading, setLoading] = createSignal(false);

    const reloadKlinkData = async () => {
        setLoading(true);
        // check existing first
        const ids = state.klinks
            // filter out local klinks
            .filter(it => !!it.readKey)
            .map(it => it.id);
        if (ids.length != 0) {
            const [err, data] = await makeRequest(() => api.queryExisting({ requestBody: ids }));
            if (err) {
                setLoading(false);
                return;
            }
            const queryExistingResponse = new Set(data);
            update(current => {
                for (const klink of current.klinks) {
                    const isShared = queryExistingResponse.has(klink.id);
                    if (!isShared) {
                        klink.readKey = null;
                        klink.writeKey = null;
                    }
                }
            });
        }

        // TODO: Should be done in bulk
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
                    found.updatedAt = time.unixFromResponse(updated.updatedAt);
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
