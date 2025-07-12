import { FolderOpen } from "lucide-solid";
import { Component, Show } from "solid-js";
import KlinkSidebar from "~/components/KlinkSidebar";
import KlinkCollection from "./components/KlinkCollection";
import { useKlinks } from "~/stores/klink-store";
import useKlinkIdParam from "~/hooks/use-klinkid-params";

const KlinkCollectionRoute: Component = () => {
  const klinks = useKlinks();
  const klinkId = useKlinkIdParam();
  const shouldShowPlaceholder = () => klinks().length > 0 && !!klinkId();

  return (
    <div class="flex flex-col lg:flex-row h-screen">
      <div class="w-full lg:w-1/6 lg:h-full border-base-300 lg:border-r-2">
        <KlinkSidebar />
      </div>
      <div class="w-full lg:w-2/6 h-full lg:border-base-300 lg:border-r-2">
        <KlinkCollection />
      </div>
      <Show when={shouldShowPlaceholder()}>
        <div class="hidden lg:block lg:w-2/6 h-full border-base-300 lg:border-r-2">
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
};

export default KlinkCollectionRoute;
