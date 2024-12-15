import { FolderOpen } from "lucide-solid";
import { Component, Show } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkSidebar from "~/components/KlinkSidebar";
import useSelector from "~/lib/klinks/useSelector";

const KlinkCollectionRoute: Component = () => {
  const shouldShowPlaceholder = useSelector(store =>
    store.klinks.length > 0 && !!store.selectedKlinkId
  );

  return (
    <div class="flex flex-col lg:flex-row h-screen">
      <div class="w-full lg:w-1/6 lg:h-full border-zinc-900 lg:border-r-2 border-b-2">
        <KlinkSidebar />
      </div>
      <div class="w-full lg:w-2/6 h-full border-zinc-900 lg:border-r-2">
        <KlinkCollection />
      </div>
      <Show when={shouldShowPlaceholder()}>
        <div class="hidden lg:block lg:w-2/6 h-full border-zinc-900 lg:border-r-2">
          <div class="flex h-full w-full items-center justify-center">
            {/* Select Placeholder */}
            <div class="flex flex-col items-center space-y-8">
              <FolderOpen size={60} />
              <p class="font-medium text-lg">Select a Klink to view.</p>
            </div>
          </div>
        </div>
      </Show>
    </div>
  );
}

export default KlinkCollectionRoute;
