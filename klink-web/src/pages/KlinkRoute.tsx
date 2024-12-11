import { Component, Show } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkEntries from "~/components/KlinkEntries";
import KlinkSidebar from "~/components/KlinkSidebar";
import useKlink from "~/lib/klinks/useKlink";
import useKlinkIdParam from "~/lib/useKlinkIdParam";

const KlinkRoute: Component = () => {
  const klinkId = useKlinkIdParam();
  const klink = useKlink(klinkId);

  return (
    <div class="flex flex-row h-screen">
      {/* Sidebar */}
      <div class="w-1/6 h-full border-zinc-900 border-r-2">
        <KlinkSidebar />
      </div>

      {/* KlinkCollection */}
      <div class="w-2/6 h-full border-zinc-900 border-r-2">
        <KlinkCollection />
      </div>

      {/* Klink Details */}
      <div class="w-2/6 h-full border-zinc-900 border-r-2">
        {/* TODO: Modify fallback -> Custom component for import, if keys are present. Otherwise empty. */}
        <Show when={!!klink()} fallback={<div class="">Klink Not Found.</div>}>
          <KlinkEntries klink={klink} />
        </Show>
      </div>
    </div>
  );
}

export default KlinkRoute;
