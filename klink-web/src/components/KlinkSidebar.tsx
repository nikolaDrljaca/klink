import { A } from "@solidjs/router";
import { Info, Settings } from "lucide-solid";
import { Component } from "solid-js";

const KlinkSidebar: Component = () => {
  return (<>
    <div class="flex flex-col h-full w-full justify-between items-center p-4">
      <A href="/c" class="btn btn-ghost font-semibold text-2xl">Klink</A>

      <div class="flex flex-col w-full justify-between gap-y-2 items-center">
        <A href="/settings" class="btn btn-ghost btn-sm w-full font-semibold">
          <Settings size={20} />
          Settings
        </A>
        <A href="/about" class="btn btn-ghost btn-sm w-full font-semibold">
          <Info size={20} />
          About
        </A>
        <div class="flex items-center gap-x-2">
          <p class="text-xs font-thin">Made by __ | Version 0.1</p>
        </div>
      </div>
    </div>
  </>);
}

export default KlinkSidebar;
