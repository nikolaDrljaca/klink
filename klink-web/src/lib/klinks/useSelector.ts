import { useAppStore } from "~/lib/klinks/context";
import { KlinkCollectionStore } from "~/lib/klinks/store";

// Pick a piece of the KlinkCollectionStore as a slice-type state.
export default function useSelector<T>(selector: (store: KlinkCollectionStore) => T): () => T {
    const store = useAppStore();
    return () => selector(store.state);
}
