import { createResource } from "solid-js";
import useKlinkKeyParams from "~/hooks/use-klink-key-params";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import makeKlinkApi from "~/lib/make-klink-api";
import makeRelativeTime from "~/lib/relative-time";
import { useAppStore } from "~/stores/app-store-context";
import { Klink } from "~/types/domain";

export default function importKlinkStore() {
    const { readKey, writeKey } = useKlinkKeyParams();
    const klinkId = useKlinkIdParam();
    const api = makeKlinkApi();
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

    const relativeTime = makeRelativeTime();

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
            updatedAt: relativeTime.unixFromResponse(data.latest.updatedAt),
            readKey: data.latest.readKey,
            writeKey: data.latest.writeKey
        }
        store.update(state => {
            state.klinks.push(klink);
        });
    }

    const updatedAt = () => relativeTime.format(relativeTime.unixFromResponse(data.latest.updatedAt));

    return {
        data,
        updatedAt,
        readKey,
        writeKey,
        importKlink,
        refetch
    }
}
