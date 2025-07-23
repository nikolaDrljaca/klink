import { ParentComponent, Show } from "solid-js";
import KlinkCollection from "./components/KlinkCollection";
import { createMediaQuery } from "@solid-primitives/media";

export const KlinkCollectionRouteParent: ParentComponent = (props) => {
  const isSmall = createMediaQuery("(max-width: 1023px)");
  return (
    <>
      <Show when={!isSmall()}>
        <div class="w-full lg:w-2/6 h-full lg:border-base-300 lg:border-r-2">
          <KlinkCollection />
        </div>
      </Show>
      {props.children}
    </>
  );
};

export const KlinkCollectionRoute: ParentComponent = (props) => {
  const isSmall = createMediaQuery("(max-width: 1023px)");
  return (
    <>
      <Show when={isSmall()}>
        <div class="w-full lg:w-2/6 h-full lg:border-base-300 lg:border-r-2">
          <KlinkCollection />
        </div>
      </Show>
      {props.children}
    </>
  );
};
