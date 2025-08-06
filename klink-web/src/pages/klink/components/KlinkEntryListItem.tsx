import { Image } from "@unpic/solid";
import { Trash } from "lucide-solid";
import { Component, createResource, Show, Suspense } from "solid-js";
import { KlinkEntry } from "~/generated";

type KlinkEntryListItemProps = {
  entry: KlinkEntry;
  isReadOnly: boolean;
  onDeleteClick: () => void;
};

const asFaviconRequestUrl = (url: string) =>
  `https://t2.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=${url}&size=32`;

const KlinkEntryListItem: Component<KlinkEntryListItemProps> = (props) => {
  const faviconUrl = () => asFaviconRequestUrl(props.entry.value);

  const title = () => {
    if (props.entry.title) {
      return props.entry.title;
    }
    return props.entry.value;
  };

  const description = () => {
    if (props.entry.description) {
      return props.entry.description;
    }
    return props.entry.value;
  };

  const LoadingBar: Component = () => <div class="skeleton h-20 w-full"></div>;

  return (
    <li class="card card-compact bg-base-300 w-full">
      <div class="flex flex-col space-y-2 p-4">
        <div class="flex flex-row items-center justify-between">
          {/* Image */}
          <div class="relative h-6 w-6 flex-shrink-0">
            <Image
              src={faviconUrl()}
              width={32}
              height={32}
              alt=""
            />
          </div>

          {/* TODO: Too much space taken by button. Redesign!  */}
          <Show when={!props.isReadOnly}>
            <button
              class="btn btn-circle btn-sm btn-ghost text-error"
              onClick={props.onDeleteClick}
            >
              <Trash size={12} />
            </button>
          </Show>
        </div>

        {/* Url value */}
        <Suspense fallback={<LoadingBar />}>
          <div class="flex-grow space-y-1 min-w-0 text-base-content">
            <h3 class="font-semibold">
              <a
                href={props.entry.value}
                target="_blank"
                rel="noopener noreferrer"
                class="hover:underline block break-words whitespace-normal"
              >
                <span>{title()}</span>
              </a>
            </h3>
            <p class="text-xs font-light pt-1">{description()}</p>
          </div>
        </Suspense>
      </div>
    </li>
  );
};

export default KlinkEntryListItem;
