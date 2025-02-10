import { createSignal } from "solid-js";
import { QueryExistingPayload, QueryExistingPayloadKlinksInner } from "~/generated";
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
        // local procedure to create request payload
        const createPaylaod = (klinks: Klink[]): QueryExistingPayload => {
            const out: QueryExistingPayloadKlinksInner[] = []
            for (const item of klinks) {
                // filter out local klinks
                if (!item.readKey) {
                    continue;
                }
                out.push({
                    id: item.id,
                    readKey: item.readKey
                });
            }
            return { klinks: out }
        }
        const payload = createPaylaod(state.klinks);
        // if there are no shared klinks -- skip request
        if (payload.klinks.length === 0) {
            setLoading(false);
            return;
        }
        const [err, data] = await makeRequest(api.queryExistingRaw({ queryExistingPayload: createPaylaod(state.klinks) }));
        if (err) {
            setLoading(false);
            return;
        }
        const sharedKlinks = new Map(data.map(it => [it.id, it]));
        update(current => {
            for (const item of current.klinks) {
                const isShared = sharedKlinks.has(item.id);
                if (isShared) {
                    // local klink is still shared -- update its data
                    const updated = sharedKlinks.get(item.id);
                    item.name = updated.name;
                    item.description = updated.description;
                    item.updatedAt = time.unixFromResponse(updated.updatedAt);
                } else {
                    // local klink is no longer shared -- delete its keys
                    item.readKey = null;
                    item.writeKey = null;
                }
            }
        });
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
                description: temp.description ?? "",
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
            const names = new Set(state.klinks.map(it => it.name));
            if (names.has(klink.name)) {
                return;
            }
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
