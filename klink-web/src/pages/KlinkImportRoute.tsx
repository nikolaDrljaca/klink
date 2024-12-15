import { Component } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkImport from "~/components/KlinkImport";
import KlinkSidebar from "~/components/KlinkSidebar";


const KlinkImportRoute: Component = () => {
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

      {/* Import Details */}
      <div class="w-2/6 h-full border-zinc-900 border-r-2">
        <KlinkImport />
      </div>
    </div>
  );
}

export default KlinkImportRoute;
