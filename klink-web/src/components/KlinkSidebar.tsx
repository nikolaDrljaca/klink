import { A } from "@solidjs/router";
import { Image } from "@unpic/solid";
import { Info, Settings } from "lucide-solid";
import { Component } from "solid-js";
import logo from "/images/logo.png";

const KlinkSidebar: Component = () => {
  return (
    <div class="flex flex-row lg:flex-col lg:h-full w-full items-start justify-between lg:justify-start p-4">
      <A href="/c" class="btn btn-ghost font-semibold text-2xl">
        <Image src={logo} width={32} height={32} />
        Klink
      </A>

      <div class="h-8"></div>

      <div class="flex flex-row lg:flex-col items-start justify-center lg:w-full space-x-1 lg:space-x-0 lg:space-y-1">
        <A href="/settings" class="btn btn-ghost btn-md font-semibold justify-start lg:w-full">
          <Settings size={20} />
          Settings
        </A>
        <A href="/about" class="btn btn-ghost btn-md font-semibold justify-start lg:w-full">
          <Info size={20} />
          About
        </A>
      </div>
    </div>
  );
}

export default KlinkSidebar;
