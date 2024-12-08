import { A } from "@solidjs/router";
import { Info, Settings } from "lucide-solid";
import { Component } from "solid-js";

const KlinkSidebar: Component = () => {
  return (<>
    <div class="flex flex-col h-full w-full justify-between items-start p-4">
      <A href="/c" class="btn btn-ghost font-semibold text-2xl">Klink</A>

      <div class="flex flex-col justify-between gap-y-2">
        <A href="/settings" class="btn btn-ghost btn-sm font-semibold justify-start">
          <Settings size={20} />
          Settings
        </A>
        <A href="/about" class="btn btn-ghost btn-sm font-semibold justify-start">
          <Info size={20} />
          About
        </A>
      </div>
    </div>
  </>);
}

export default KlinkSidebar;
