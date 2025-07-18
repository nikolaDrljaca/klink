import { Component, Show } from "solid-js";
import KlinkSidebar from "~/components/KlinkSidebar";
import KlinkCollection from "./components/KlinkCollection";

const KlinkCollectionRoute: Component = () => {
  return (
    <div class="flex flex-col lg:flex-row h-screen">
      <div class="w-full lg:w-1/6 lg:h-full border-base-300 lg:border-r-2">
        <KlinkSidebar />
      </div>
      <div class="w-full lg:w-2/6 h-full lg:border-base-300 lg:border-r-2">
        <KlinkCollection />
      </div>
    </div>
  );
};

export default KlinkCollectionRoute;
