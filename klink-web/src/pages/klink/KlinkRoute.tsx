import { Component, Show } from "solid-js";
import KlinkSidebar from "~/components/KlinkSidebar";
import KlinkCollection from "~/pages/collection/components/KlinkCollection";
import KlinkEntries from "~/pages/klink/components/KlinkEntries";
import KlinkNotFound from "./components/KlinkNotFound";
import { useSelectedKlink } from "~/stores/klink-hooks";

const KlinkRoute: Component = () => {
  const klink = useSelectedKlink();

  return (
    <div class="flex flex-col lg:flex-row h-screen">
      {/* Sidebar */}
      <div class="w-full lg:w-1/6 lg:h-full border-base-300 lg:border-r-2">
        <KlinkSidebar />
      </div>

      {/* KlinkCollection */}
      <div class="hidden lg:block lg:w-2/6 h-full border-base-300 lg:border-r-2">
        <KlinkCollection />
      </div>

      {/* Klink Details */}
      <div class="w-full lg:w-3/6 xl:w-2/6 h-full border-base-300 lg:border-r-2">
        {/* TODO: Modify fallback */}
        <Show when={!!klink()} fallback={<KlinkNotFound />}>
          <KlinkEntries />
        </Show>
      </div>

      <div class="hidden xl:block px-4 pt-4">
        <div class="card bg-neutral text-neutral-content w-60">
          <div class="p-4 flex flex-col gap-y-2">
            <h2 class="text-lg">Tips:</h2>
            <div class="flex flex-row items-center gap-x-2">
              <kbd class="kbd kbd-sm">ctrl</kbd>
              +
              <kbd class="kbd kbd-sm">v</kbd>
              <p class="text-md">Paste to Klink</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default KlinkRoute;
