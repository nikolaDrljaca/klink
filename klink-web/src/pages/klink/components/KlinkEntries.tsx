import { GlobeLock, Globe, ArrowLeft, Share2 } from "lucide-solid";
import { Accessor, Component, createMemo, createSignal, For, Match, Show, Switch } from "solid-js";
import { Klink } from "~/types/domain";
import { createKlinkEntriesStore } from "~/pages/klink/stores/klink-entries-store";
import KlinkEntryListItem from "~/pages/klink/components/KlinkEntryListItem";
import { useNavigate } from "@solidjs/router";
import makeModal from "~/components/modal/Modal";
import ShareKlinkModal from "~/pages/components/klink-share-modal/ShareKlinkModal";

type KlinkDetailsProps = {
  klink: Accessor<Klink>
}

const KlinkEntries: Component<KlinkDetailsProps> = (props) => {
  const entries = createMemo(() => createKlinkEntriesStore(props.klink()));

  const [inputUrl, setInputUrl] = createSignal("");
  const isShared = () => props.klink().readKey;
  const isReadOnly = () => props.klink().readKey && !props.klink().writeKey;

  const inputPlaceholder = () => isReadOnly() ? "You don't have access" : "Paste or Type here";

  const navigate = useNavigate();

  const shareModal = makeModal();

  const handlePaste = (e: ClipboardEvent) => {
    e.preventDefault();
    e.stopPropagation();
    const pastedValue = e.clipboardData.getData('text/plain');
    entries().onAddEntry(pastedValue);
  }

  const handleEnter = (event: Event) => {
    event.preventDefault();
    event.stopPropagation();
    entries().onAddEntry(inputUrl());
    setInputUrl("");
  }

  const handleBack = () => {
    if (history.length <= 2) {
      navigate('/c', {
        replace: true
      });
    } else {
      navigate(-1);
    }
  }

  const deleteEntry = (value: string) => {
    if (isReadOnly()) {
      return;
    }
    entries().onRemoveEntry(value);
  }

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">

      {/* Share Modal - Shown only on Small Screen */}
      <shareModal.Modal>
        <ShareKlinkModal klinkId={props.klink().id} />
      </shareModal.Modal>

      {/* Top Row - Name */}
      <div class="flex w-full justify-between items-center px-4 pt-4 pb-2">
        <div class="flex items-center gap-x-2">
          <button class="lg:hidden btn btn-square btn-ghost" onClick={handleBack}>
            <ArrowLeft size={24} />
          </button>
          <p class="text-2xl"># {props.klink().name}</p>
        </div>
        <div class="flex items-center gap-x-2">
          <Switch>
            <Match when={isShared()}>
              <div class="tooltip tooltip-left" data-tip="This collection is shared.">
                <button class="btn btn-square btn-primary no-animation">
                  <Globe size={24} />
                </button>
              </div>
            </Match>
            <Match when={!isShared()}>
              <div class="tooltip tooltip-left" data-tip="This collection is local.">
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

      {/* Description - Only Small Screen */}
      <Show when={props.klink().description}>
        {it =>
          <p class="lg:hidden text-lg text-zinc-400 px-4 pb-2">{it()}</p>
        }
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
              class="input input-bordered w-full" />
            <div class="label">
              <span class="label-text-alt">Only accepts URLs</span>
            </div>
          </label>
        </form>
      </div>

      {/* Link List - Container */}
      <ul class="lg:container items-center w-full px-4 space-y-2">
        {/* List Item */}
        <For each={entries().state}>
          {(item,) =>
            <KlinkEntryListItem
              entry={item}
              isReadOnly={isReadOnly()}
              onDeleteClick={() => deleteEntry(item.value)}
            />
          }
        </For>
      </ul>
    </div>
  );
}

export default KlinkEntries;
