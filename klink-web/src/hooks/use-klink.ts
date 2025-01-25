import { useAppStore } from "~/stores/app-store-context";
import { Klink } from "~/types/domain";

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
