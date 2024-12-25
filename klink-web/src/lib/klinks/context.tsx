import { createContext, ParentComponent, useContext } from "solid-js";
import { AppStore, createAppStore } from "~/lib/klinks/store"

const KlinkCollectionStoreContext = createContext<AppStore>(null);

export function useAppStore(): AppStore {
  const current = useContext(KlinkCollectionStoreContext);
  if (!current) {
    console.error("Accessing Dashboard store outside of provided scope!");
  }
  return current;
}

export const KlinkCollectionStoreProvider: ParentComponent = (props) => {
  const store = createAppStore();

  return (
    <KlinkCollectionStoreContext.Provider value={store}>
      {props.children}
    </KlinkCollectionStoreContext.Provider>
  );
}
