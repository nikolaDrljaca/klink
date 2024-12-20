import { Component, Show } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkEntries from "~/components/KlinkEntries";
import KlinkSidebar from "~/components/KlinkSidebar";
import useSelector from "~/lib/klinks/useSelector";
import useKlinkIdParam from "~/lib/useKlinkIdParam";

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
        {/* TODO: Modify fallback -> Custom component for import, if keys are present. Otherwise empty. */}
        <Show when={!!klink()} fallback={<div class="">Klink Not Found.</div>}>
          <KlinkEntries klink={klink} />
        </Show>
      </div>
    </div>
  );
}

export default KlinkRoute;
