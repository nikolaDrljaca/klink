import { A } from "@solidjs/router";
import { Image } from "@unpic/solid";
import { Component, createSignal, Show } from "solid-js";
import logo from "/images/logo.png";
import { makePersisted } from "@solid-primitives/storage";
import { Settings } from "lucide-solid";

const extensionUrl =
  "https://chromewebstore.google.com/detail/klink-extension/hiamgjmbmkjfbcmopcfbodpdgbjlmjko";

const KlinkSidebar: Component = () => {
  const [extensionSeen, setExtensionSeen] = makePersisted(
    createSignal(false),
    {
      name: "ext",
      storage: localStorage,
    },
  );

  const onGoToExtension = () => {
    setExtensionSeen(true);
  };

  return (
    <div class="flex flex-row lg:flex-col lg:h-full w-full items-start justify-between lg:justify-start p-4">
      <A href="/c" class="btn btn-ghost font-semibold text-2xl">
        <Image src={logo} width={32} height={32} />
        Klink
      </A>

      <div class="h-8"></div>

      <div class="flex flex-row lg:flex-col items-start justify-center lg:w-full space-x-1 lg:space-x-0 lg:space-y-1">
        <Show when={!extensionSeen()}>
          <A
            onClick={onGoToExtension}
            href={extensionUrl}
            class="btn btn-ghost btn-md font-semibold justify-start lg:w-full"
            target="_"
          >
            <Image
              src={`https://cdn.simpleicons.org/chromewebstore/A6ADBB`}
              width={24}
              height={24}
            />
            Get the Extension
          </A>
        </Show>

        {/* TODO: Enable when in place */}
        <A
          href="/c/settings"
          class="btn btn-ghost btn-md font-semibold justify-start lg:w-full"
        >
          <Settings size={20} />
          Settings
        </A>
        {/* <A href="/about" class="btn btn-ghost btn-md font-semibold justify-start lg:w-full"> */}
        {/*   <Info size={20} /> */}
        {/*   About */}
        {/* </A> */}
      </div>
    </div>
  );
};

export default KlinkSidebar;
