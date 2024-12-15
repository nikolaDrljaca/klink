import { A } from "@solidjs/router";
import { Info, Settings } from "lucide-solid";
import { Component } from "solid-js";

const KlinkSidebar: Component = () => {
  return (<>
    <div class="flex flex-row lg:flex-col lg:h-full w-full justify-between items-start p-4">
      <A href="/c" class="btn btn-ghost font-semibold text-2xl">Klink</A>

      <div class="flex flex-row lg:flex-col items-start justify-center lg:w-full pt-2 lg:pt-0">
        <A href="/settings" class="btn btn-ghost btn-sm font-semibold">
          <Settings size={20} />
          Settings
        </A>
        <A href="/about" class="btn btn-ghost btn-sm font-semibold">
          <Info size={20} />
          About
        </A>
      </div>
    </div>
  </>);
}

export default KlinkSidebar;
