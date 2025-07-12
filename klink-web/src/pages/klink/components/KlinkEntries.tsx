import { ArrowLeft, Globe, GlobeLock, Share2 } from "lucide-solid";
import {
  Component,
  createSignal,
  For,
  Match,
  onCleanup,
  onMount,
  Show,
  Switch,
} from "solid-js";
import KlinkEntryListItem from "~/pages/klink/components/KlinkEntryListItem";
import { useNavigate } from "@solidjs/router";
import makeModal from "~/components/modal/Modal";
import ShareKlinkModal from "~/pages/components/klink-share-modal/ShareKlinkModal";
import useKlinkEntries from "~/stores/klink-entry-store";

const KlinkEntries: Component = () => {
  const store = useKlinkEntries();

  const [inputUrl, setInputUrl] = createSignal("");
  const inputPlaceholder = () =>
    isReadOnly() ? "You don't have access" : "Paste or Type here";

  const isShared = () => store().klink.readKey;
  const isReadOnly = () => store().klink.readKey && !store().klink.writeKey;

  const navigate = useNavigate();

  const shareModal = makeModal();

  const handlePaste = (e: ClipboardEvent) => {
    e.preventDefault();
    e.stopPropagation();
    const pastedValue = e.clipboardData.getData("text/plain");
    store().addEntry(pastedValue);
  };

  const handleEnter = async (event: Event) => {
    event.preventDefault();
    event.stopPropagation();
    await store().addEntry(inputUrl());
    setInputUrl("");
  };

  const handleDelete = async (value: string) => {
    await store().removeEntry(value);
  };

  const handleBack = () => {
    if (history.length <= 2) {
      navigate("/c", {
        replace: true,
      });
    } else {
      navigate(-1);
    }
  };

  onMount(() => {
    store().fetchEntries();
  });

  onCleanup(() => {
    store().cleanup();
  });

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">
      {/* Share Modal - Shown only on Small Screen */}
      <shareModal.Modal>
        <ShareKlinkModal klinkId={store().klink.id} />
      </shareModal.Modal>

      {/* Top Row - Name */}
      <div class="flex w-full justify-between items-center px-4 pt-4 pb-2">
        <div class="flex items-center gap-x-2">
          <button
            class="lg:hidden btn btn-square btn-ghost"
            onClick={handleBack}
          >
            <ArrowLeft size={24} />
          </button>
          {/* Title - Only large screen */}
          <p class="hidden lg:block text-2xl"># {store().klink.name}</p>
        </div>
        <div class="flex items-center gap-x-2">
          <Switch>
            <Match when={isShared()}>
              <div
                class="tooltip tooltip-left"
                data-tip="This collection is shared."
              >
                <button class="btn btn-square btn-primary no-animation">
                  <Globe size={24} />
                </button>
              </div>
            </Match>
            <Match when={!isShared()}>
              <div
                class="tooltip tooltip-left"
                data-tip="This collection is local."
              >
                <button class="btn btn-square btn-ghost no-animation">
                  <GlobeLock size={24} />
                </button>
              </div>
            </Match>
          </Switch>
          {/* Share Modal */}
          <button class="lg:hidden btn" onClick={shareModal.controller.open}>
            <Share2 size={24} />
            <p class="sm:block hidden">Share</p>
          </button>
        </div>
      </div>

      {/* Title - Only Small Screen */}
      <p class="lg:hidden text-2xl px-5 py-2"># {store().klink.name}</p>
      {/* Description - Only Small Screen */}
      <Show when={store().klink.description}>
        {(it) => <p class="lg:hidden text-lg text-zinc-400 px-5 pb-2">{it()}
        </p>}
      </Show>

      <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center w-full">
        <form onSubmit={handleEnter} class="w-full">
          <label class="form-control w-full">
            <input
              type="url"
              placeholder={inputPlaceholder()}
              value={inputUrl()}
              onInput={(event) => setInputUrl(event.target.value)}
              onPaste={handlePaste}
              disabled={isReadOnly()}
              class="input input-bordered w-full"
            />
            <div class="label">
              <span class="label-text-alt">Only accepts URLs</span>
            </div>
          </label>
        </form>
      </div>

      {/* Link List - Container */}
      <ul class="lg:container items-center w-full px-4 space-y-2">
        {/* List Item */}
        <For each={store().entries}>
          {(item) => (
            <KlinkEntryListItem
              entry={item}
              isReadOnly={isReadOnly()}
              onDeleteClick={() => handleDelete(item.value)}
            />
          )}
        </For>
      </ul>
    </div>
  );
};

export default KlinkEntries;
