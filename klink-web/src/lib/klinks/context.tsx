import { createContext, ParentComponent, useContext } from "solid-js";
import { createAppStore, KlinkCollectionActions, KlinkCollectionStore } from "~/lib/klinks/store"

const KlinkCollectionStoreContext = createContext<KlinkCollectionStore>(null);
const KlinkCollectionActionsContext = createContext<KlinkCollectionActions>(null);

export function useKlinkCollectionStore(): KlinkCollectionStore {
  const current = useContext(KlinkCollectionStoreContext);
  if (!current) {
    console.error("Accessing Dashboard store outside of provided scope!");
  }
  return current;
}

export function useKlinkCollectionActions(): KlinkCollectionActions {
  const current = useContext(KlinkCollectionActionsContext);
  if (!current) {
    console.error("Accessing Dashboard store outside of provided scope!");
  }
  return current;
}

export const KlinkCollectionStoreProvider: ParentComponent = (props) => {
  const store = createAppStore();

  return (
    <KlinkCollectionStoreContext.Provider value={store.state} >
      <KlinkCollectionActionsContext.Provider value={store.actions}>
        {props.children}
      </KlinkCollectionActionsContext.Provider>
    </KlinkCollectionStoreContext.Provider>
  );
}
