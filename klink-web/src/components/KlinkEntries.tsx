import { GlobeLock, Globe } from "lucide-solid";
import { Accessor, Component, createMemo, createSignal, For, Match, Switch } from "solid-js";
import KlinkEntryListItem from "~/components/KlinkEntryListItem";
import { createKlinkEntriesStore } from "~/lib/entries/klinkEntriesStore";
import { Klink } from "~/lib/klinks/store";

type KlinkDetailsProps = {
  klink: Accessor<Klink>
}

const KlinkEntries: Component<KlinkDetailsProps> = (props) => {
  const entries = createMemo(() => createKlinkEntriesStore(props.klink()));

  const [inputUrl, setInputUrl] = createSignal("");
  const isShared = () => props.klink().readKey && props.klink().writeKey;

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

  const deleteEntry = (value: string) => {
    entries().onRemoveEntry(value);
  }

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">

      {/* Top Row */}
      <div class="flex w-full justify-between items-center px-4 pt-4 pb-2">
        <p class="text-2xl"># {props.klink().name}</p>
        <Switch>
          <Match when={isShared()}>
            <div class="tooltip tooltip-left hover:cursor-pointer" data-tip="This collection is shared.">
              <button class="btn btn-square btn-primary no-animation">
                <Globe size={24} />
              </button>
            </div>
          </Match>
          <Match when={!isShared()}>
            <div class="tooltip tooltip-left hover:cursor-pointer" data-tip="This collection is local.">
              <button class="btn btn-square btn-ghost no-animation">
                <GlobeLock size={24} />
              </button>
            </div>
          </Match>
        </Switch>
      </div>

      {/* Button Row */}
      <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center w-full">
        <form onSubmit={handleEnter} class="w-full">
          <label class="form-control w-full">
            <input
              type="url"
              placeholder="Paste or Type here"
              value={inputUrl()}
              onInput={(event) => setInputUrl(event.target.value)}
              onPaste={handlePaste}
              class="input input-bordered w-full" />
            <div class="label">
              <span class="label-text-alt">Only accepts URLs</span>
            </div>
          </label>
        </form>
      </div>

      {/* Link List - Container */}
      <div class="lg:container items-center w-full px-4 space-y-2">
        {/* List Item */}
        <For each={entries().state}>
          {(item,) =>
            <KlinkEntryListItem
              entry={item}
              onDeleteClick={() => deleteEntry(item.value)}
            />
          }
        </For>
      </div>
    </div>
  );
}

export default KlinkEntries;
