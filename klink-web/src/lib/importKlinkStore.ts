import useKlinkKeyParams from "~/lib/useKlinkKeyParams";
import useKlinkIdParam from "~/lib/useKlinkIdParam";
import { createResource } from "solid-js";
import { useAppStore } from "~/lib/klinks/context";
import klinkApi from "~/lib/klinkApi/api";
import { Klink } from "~/types/domain";
import { formatRelative, unixFromReponse } from "~/lib/relativeTime";

export default function importKlinkStore() {
    const { readKey, writeKey } = useKlinkKeyParams();
    const klinkId = useKlinkIdParam();
    const api = klinkApi();
    const request = async () => {
        const curr = klinkId();
        if (!curr) {
            return Promise.reject();
        }
        return api.getKlink({
            klinkId: curr,
            readKey: readKey,
            writeKey: writeKey
        });
    }
    const [data, { refetch }] = createResource(request);
    const store = useAppStore();
    const existingIds = store.state.klinks.map(it => it.id);

    const importKlink = () => {
        // if the klink already exists, ignore
        if (existingIds.includes(data.latest.id)) {
            return;
        }
        if (!data.latest) {
            return;
        }
        const klink: Klink = {
            id: data.latest.id,
            name: data.latest.name,
            description: data.latest.description,
            updatedAt: unixFromReponse(data.latest.updatedAt),
            readKey: data.latest.readKey,
            writeKey: data.latest.writeKey
        }
        store.update(state => {
            state.klinks.push(klink);
        });
    }

    const updatedAt = () => formatRelative(unixFromReponse(data.latest.updatedAt));

    return {
        data,
        updatedAt,
        readKey,
        writeKey,
        importKlink,
        refetch
    }
}
