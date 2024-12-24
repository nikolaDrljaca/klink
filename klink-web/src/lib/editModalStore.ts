import { createStore } from "solid-js/store";
import klinkApi from "~/lib/klinkApi/api";
import { UpdateKlinkRequest } from "~/generated";
import useKlink from "~/lib/klinks/useKlink";

type EditKlinkModalEvent =
    | { type: 'success' }
    | { type: 'failure' }

export default function editKlinkStore(klinkId: string) {
    const { klink, update } = useKlink(klinkId);
    const api = klinkApi();

    const [store, setStore] = createStore({
        name: klink.name,
        description: klink.description,
        loading: false,
        get isReadOnly() {
            return klink.readKey && !klink.writeKey
        },
        get isEditDisabled() {
            const nameInvalid = store.name.length < 3;
            return store.isReadOnly || nameInvalid;
        }
    });
    const isShared = !!klink.readKey && !!klink.writeKey;

    const setName = (value: string) => setStore('name', value);
    const setDescription = (value: string) => setStore('description', value);

    const updateKlink = (value: { name: string, description?: string }) => {
        update(current => {
            current.name = value.name;
            current.description = value.description;
        });
    }

    const submit = async (): Promise<EditKlinkModalEvent> => {
        if (store.isEditDisabled || store.loading) {
            return;
        }
        if (isShared) {
            const payload: UpdateKlinkRequest = {
                klinkId: klinkId,
                readKey: klink.readKey,
                writeKey: klink.writeKey,
                patchKlinkPayload: {
                    name: store.name,
                    description: store.description
                }
            }
            try {
                setStore('loading', true);
                const updated = await api.updateKlink(payload);
                updateKlink({ name: updated.name, description: updated.description });
                setStore('loading', false);
                return { type: 'success' }
            } catch (e) {
                setStore('loading', false);
                return { type: 'failure' }
            }
        }
        // not shared - update local klink only
        updateKlink({ name: store.name, description: store.description });
        return { type: 'success' }
    }

    return {
        state: store,
        setName,
        setDescription,
        submit,
        klink
    }
}
