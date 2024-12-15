import useKlinkKeyParams from "~/lib/useKlinkKeyParams";
import useKlinkIdParam from "~/lib/useKlinkIdParam";
import { KlinkApi } from "~/generated";
import { createResource } from "solid-js";
import { Klink } from "./klinks/store";
import { useAppStore } from "./klinks/context";

export default function importKlinkStore() {
    const { readKey, writeKey } = useKlinkKeyParams();
    const klinkId = useKlinkIdParam();
    const api = new KlinkApi();
    const request = async () => {
        const curr = klinkId();
        if (!curr) {
            return Promise.reject();
        }
        return api.getKlink({
            klinkId: curr
        });
    }
    const [data, { refetch }] = createResource(request);
    const store = useAppStore();

    const importKlink = () => {
        if (!data.latest) {
            return;
        }
        const klink: Klink = {
            id: data.latest.id,
            name: data.latest.name,
            description: data.latest.description,
            readKey: data.latest.readKey,
            writeKey: data.latest.writeKey
        }
        store.update(state => {
            state.klinks.push(klink);
        });
    }

    return {
        data,
        readKey,
        writeKey,
        importKlink,
        refetch
    }
}
