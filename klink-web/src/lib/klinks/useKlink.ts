import { useAppStore } from "~/lib/klinks/context";
import { Klink } from "~/lib/klinks/store";

export default function useKlink(klinkId: string) {
    const store = useAppStore();
    const current = store.state.klinks.find(it => it.id === klinkId)!;
    const update = (fn: (klink: Klink) => void) => {
        store.update(state => {
            const current = state.klinks.find(it => it.id === klinkId)!;
            fn(current);
        });
    }
    return { klink: current, update }
}
