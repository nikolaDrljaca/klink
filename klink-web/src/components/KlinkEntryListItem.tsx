import { createAsync, query } from "@solidjs/router";
import { Image } from "@unpic/solid";
import { Component, Show, Suspense } from "solid-js";
import { KlinkEntry } from "~/generated";
import { getPageMetadata } from "~/lib/pageMetadata";

type KlinkEntryListItemProps = {
  entry: KlinkEntry
}

// define a query - `query` can be used to cache request responses
const getDetails = query((url: string) => getPageMetadata(url), "pageMetadataByUrl");

const KlinkEntryListItem: Component<KlinkEntryListItemProps> = (props) => {
  // createAsync - new primitive that will replace `createResource` - works with `query` to cache 
  const details = createAsync(() => getDetails(props.entry.value));

  const LoadingBar: Component = () => <div class="skeleton h-5 w-full"></div>;

  return (
    <div class="card card-compact bg-neutral w-full">
      <div class="flex items-center space-x-4 p-4">

        {/* Image */}
        <div class="relative h-6 w-6 flex-shrink-0">
          <Image
            src={`https://www.google.com/s2/favicons?domain=${props.entry.value}&sz=32`}
            width={32}
            height={32}
            alt=""
          />
        </div>

        {/* Url value */}
        <div class="flex-grow">
          <h3 class="font-semibold">
            <a
              href={props.entry.value}
              target="_blank"
              rel="noopener noreferrer"
              class="flex items-center space-x-1 hover:underline">
              <span>{props.entry.value}</span>
            </a>
          </h3>
          <Suspense fallback={<LoadingBar />}>
            <Show when={!!details()}>
              <p class="text-xs font-light text-zinc-400">{details().title ?? "No details found."}</p>
            </Show>
          </Suspense>
        </div>

      </div>
    </div>
  );
}

export default KlinkEntryListItem;
