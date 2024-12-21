import { createStore } from "solid-js/store";
import { useAppStore } from "~/lib/klinks/context";

type EditKlinkModalEvent =
    | { type: 'success' }
    | { type: 'failure' }

export default function editKlinkStore(klinkId: string) {
    const appStore = useAppStore();
    const klink = appStore.state.klinks.find(it => it.id === klinkId)!;

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
        appStore.update(state => {
            const current = state.klinks.find(it => it.id === klinkId)!;
            current.name = value.name;
            current.description = value.description;
        });
    }

    const submit = async (): Promise<EditKlinkModalEvent> => {
        if (store.isEditDisabled || store.loading) {
            return;
        }
        if (isShared) {
            // TODO: request to server and update from response
            return { type: 'success' }
        }
        // not shared - update local klink only
        updateKlink({ name: store.name, description: store.description });
        return { type: 'success' }
    }
    return {
        state: store,
        setName,
        setDescription,
        submit
    }
}
