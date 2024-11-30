import { writeClipboard } from "@solid-primitives/clipboard";
import { Copy, GlobeLock } from "lucide-solid";
import { Component, Match, Switch } from "solid-js";
import { Klink } from "~/lib/klinks/store";

type ShareKlinkModalProps = {
  item: Klink,
}

const KlinkKeyField: Component<{ key: string, title: string }> = (props) => {

  const onCopy = async () => {
    await writeClipboard(props.key);
  }

  return (
    <div class="flex flex-col space-y-2">
      <p class="text-sm text-zinc-400">{props.title}</p>
      <div class="join">
        <input
          type="text"
          value={props.key}
          disabled={true}
          class="input input-bordered w-full max-w-xs join-item" />
        <button class="btn join-item" onClick={onCopy}>
          <Copy size={14} />
        </button>
      </div>
    </div>
  )
}

const ShareKlinkModal: Component<ShareKlinkModalProps> = (props) => {
  // TODO: create a slice off the klinkStore and provide `isShared` and upload actions
  const isShared = () => props.item.readKey && props.item.writeKey;

  return (
    <div class="flex flex-col space-y-2">
      <p class="text-lg">Share Controls</p>
      <p class="font-light text-sm text-zinc-400">Manage sharing information for this collection.</p>
      {/* Keys Row */}
      <Switch>
        {/* Shared Component */}
        <Match when={isShared()}>
          <div class="flex w-full justify-between space-x-4 py-6">
            <KlinkKeyField key={props.item.readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={props.item.writeKey} title={"Write Key"} />
          </div>
          <button class="btn btn-primary btn-sm">Share</button>
          <button class="btn btn-primary btn-sm">Share as Read Only</button>
        </Match>

        {/* Local Only */}
        <Match when={!isShared()}>
          <div class="pt-4"></div>
          <GlobeLock size={24} />
          <p class="text-lg">This klink is <b>local only</b>.</p>
          <p class="pb-2">To be able to share it with others, <b>upload</b> the collection to the cloud first.</p>
          <button class="btn btn-primary btn-sm">Upload</button>
        </Match>
      </Switch>
    </div>
  );
}

export default ShareKlinkModal;
