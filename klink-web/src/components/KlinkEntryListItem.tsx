import { makeCache } from "@solid-primitives/resource";
import { Image } from "@unpic/solid";
import { Trash } from "lucide-solid";
import { Component, createResource, Show, Suspense } from "solid-js";
import { KlinkEntry } from "~/generated";
import { getPageMetadata } from "~/lib/pageMetadata";

type KlinkEntryListItemProps = {
  entry: KlinkEntry,
  isReadOnly: boolean,
  onDeleteClick: () => void
}

// cache each request to URL metadata for 5 hours
const [getDetails, invalidate] = makeCache(
  (url: string) => getPageMetadata(url),
  {
    storage: localStorage,
    expires: 5 * 60 * 60 * 1000
  }
);

const KlinkEntryListItem: Component<KlinkEntryListItemProps> = (props) => {
  const [pageDetails, { }] = createResource(props.entry.value, getDetails);

  const title = () => pageDetails()?.title ?? props.entry.value;
  const description = () => pageDetails()?.description ?? "No details found.";
  const url = () => {
    if (title() === props.entry.value) {
      return null;
    }
    return props.entry.value;
  }

  const LoadingBar: Component = () => <div class="skeleton h-20 w-full"></div>;

  return (
    <div class="card card-compact bg-base-300 w-full">
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
        <Suspense fallback={<LoadingBar />}>
          <div class="flex-grow space-y-1 min-w-0 text-base-content">
            <h3 class="font-semibold">
              <a
                href={props.entry.value}
                target="_blank"
                rel="noopener noreferrer"
                class="hover:underline block break-words whitespace-normal">
                <span>{title()}</span>
              </a>
            </h3>
            <Show when={!!pageDetails()}>
              <p class="text-xs font-light">{description()}</p>
            </Show>
            <Show when={url()}>
              <p class="text-xs font-light underline pt-1">{url()}</p>
            </Show>
          </div>
        </Suspense>

        {/* TODO: Too much space taken by button. Redesign!  */}
        <Show when={!props.isReadOnly}>
          <button class="btn btn-circle btn-sm btn-ghost text-error" onClick={props.onDeleteClick}>
            <Trash size={12} />
          </button>
        </Show>

      </div>
    </div>
  );
}

export default KlinkEntryListItem;
