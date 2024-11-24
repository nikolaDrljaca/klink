import { createContext, ParentComponent, useContext } from "solid-js";
import { createAppStore, DashboardActions, DashboardStore } from "~/lib/dashboard/store"

const DashboardStoreContext = createContext<DashboardStore>(null);
const DashboardActionsContext = createContext<DashboardActions>(null);

export function useDashboardStore(): DashboardStore {
  const current = useContext(DashboardStoreContext);
  if (!current) {
    console.error("Accessing Dashboard store outside of provided scope!");
  }
  return current;
}

export function useDashboardActions(): DashboardActions {
  const current = useContext(DashboardActionsContext);
  if (!current) {
    console.error("Accessing Dashboard store outside of provided scope!");
  }
  return current;
}

export const DashboardStoreProvider: ParentComponent = (props) => {
  const store = createAppStore();

  return (
    <DashboardStoreContext.Provider value={store.state} >
      <DashboardActionsContext.Provider value={store.actions}>
        {props.children}
      </DashboardActionsContext.Provider>
    </DashboardStoreContext.Provider>
  );
}
