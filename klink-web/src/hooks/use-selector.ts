import { useAppStore } from "~/stores/app-store-context";
import { KlinkCollectionStore } from "~/stores/app-store";

// Pick a piece of the KlinkCollectionStore as a slice-type state.
export default function useSelector<T>(selector: (store: KlinkCollectionStore) => T): () => T {
    const store = useAppStore();
    return () => selector(store.state);
}
