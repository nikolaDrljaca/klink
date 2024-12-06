import { Component } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkSidebar from "~/components/KlinkSidebar";

const KlinkCollectionRoute: Component = () => {
  return (
    <div class="flex flex-row h-screen">
      <div class="w-1/6 h-full border-zinc-900 border-r-2">
        <KlinkSidebar />
      </div>
      <div class="w-2/6 h-full border-zinc-900 border-r-2">
        <KlinkCollection />
      </div>
    </div>
  );
}

export default KlinkCollectionRoute;
