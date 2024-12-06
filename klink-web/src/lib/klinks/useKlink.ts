import { Klink } from "~/lib/klinks/store";
import { useKlinkCollectionStore } from "./context";

export default function useKlink(klinkId: () => string): () => Klink {
    const store = useKlinkCollectionStore();
    return () => store.klinks.find(it => it.id === klinkId())!;
}
