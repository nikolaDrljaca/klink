import { useAppStore } from "~/lib/klinks/context";
import useKlinkIdParam from "~/lib/useKlinkIdParam";
import { Klink } from "~/lib/klinks/store";

export default function collectionStore() {
    const { state, update } = useAppStore();
    const pathKlinkId = useKlinkIdParam();

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
            readKey: null,
            writeKey: null
        }
        update(state => {
            state.klinks.unshift(klink);
        });
    }

    return {
        state,
        pathKlinkId,
        createKlink,
        selectKlink,
        copyKlink,
    }
}
