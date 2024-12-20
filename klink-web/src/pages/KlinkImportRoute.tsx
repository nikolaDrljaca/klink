import { Component } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkImport from "~/components/KlinkImport";
import KlinkSidebar from "~/components/KlinkSidebar";


const KlinkImportRoute: Component = () => {
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

      {/* Import Details */}
      <div class="w-full lg:w-3/6 xl:w-2/6 h-full border-base-300 lg:border-r-2">
        <KlinkImport />
      </div>
    </div>
  );
}

export default KlinkImportRoute;
