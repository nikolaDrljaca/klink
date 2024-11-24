import type { Component } from 'solid-js';
import Health from "~/components/Health";
import { DashboardStoreProvider } from "~/lib/dashboard/context"
import Dashboard from '~/pages/Dashboard';

const App: Component = () => {
  return (
    <DashboardStoreProvider>
      <div class="flex flex-col align-middle justify-center">
        <Health />
        <Dashboard />
      </div>
    </DashboardStoreProvider>
  );
};

export default App;
