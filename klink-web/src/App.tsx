import type { Component } from 'solid-js';
import Health from "~/components/Health";
import { DashboardStoreProvider } from "~/lib/dashboard/context"
import { Info, Settings } from "lucide-solid"
import Dashboard from '~/pages/Dashboard';

const Sidebar: Component = () => {
  return (<>
    <div class="flex flex-col h-full w-full justify-between items-center p-4">
      <button class="btn btn-ghost font-semibold text-2xl">Klink</button>

      <div class="flex flex-col w-full justify-between gap-y-2 items-center">
        <button class="btn btn-ghost btn-sm w-full font-semibold">
          <Settings size={20} />
          Settings
        </button>
        <button class="btn btn-ghost btn-sm w-full font-semibold">
          <Info size={20} />
          About
        </button>
        <div class="flex items-center gap-x-2">
          <p class="text-xs font-thin">Made by __ | Version 0.1</p>
        </div>
      </div>
    </div>
  </>);
}

const App: Component = () => {
  return (
    <DashboardStoreProvider>
      <div class="overflow-none flex flex-row h-screen">
        {/* Sidebar */}
        <div class="w-1/6 h-full border-zinc-900 border-r-2">
          <Sidebar />
        </div>

        {/* Dashboard */}
        <div class="w-2/6 h-full border-zinc-900 border-r-2">
          <Dashboard />
        </div>
      </div>
    </DashboardStoreProvider>
  );
};

export default App;
