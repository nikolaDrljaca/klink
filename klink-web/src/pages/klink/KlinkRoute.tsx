import { Component, Show } from "solid-js";
import KlinkSidebar from "~/components/KlinkSidebar";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import useSelector from "~/hooks/use-selector";
import KlinkCollection from "~/pages/collection/components/KlinkCollection";
import KlinkEntries from "~/pages/klink/components/KlinkEntries";
import KlinkNotFound from "./components/KlinkNotFound";


const KlinkRoute: Component = () => {
  const klinkId = useKlinkIdParam();
  const klink = useSelector(store => store.klinks.find(it => it.id === klinkId())!);

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
          <KlinkEntries klink={klink} />
        </Show>
      </div>
    </div>
  );
}

export default KlinkRoute;
