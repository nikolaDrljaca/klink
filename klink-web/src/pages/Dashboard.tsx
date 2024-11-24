import { Component, For } from "solid-js";
import { useDashboardActions, useDashboardStore } from "~/lib/dashboard/context";

const Dashboard: Component = () => {
  const state = useDashboardStore();
  const actions = useDashboardActions();

  return (<>
    Dashboard
    <button class="btn" onClick={() => actions.createKlink({ name: "foo1" })}>Add new</button>
    <span class="">Selected: {state.selectedKlinkId}</span>
    <For each={state.klinks}>
      {
        (item, _) => <>
          <div class="card bg-base-100 w-96">
            <div class="card-body">
              <p class="card-title">{JSON.stringify(item)}</p>
            </div>
            <div class="card-actions justify-end">
              <button class="btn btn-primary">Select</button>
            </div>
          </div>
        </>
      }
    </For>
  </>);
}

export default Dashboard;
