import { writeClipboard } from "@solid-primitives/clipboard";
import { Copy, GlobeLock } from "lucide-solid";
import { Component, Match, onCleanup, Switch } from "solid-js";
import toast from "solid-toast";
import shareKlinkStore from "~/lib/shareKlinkStore";

type ShareKlinkModalProps = {
  klinkId: string,
}

const KlinkKeyField: Component<{ key: string, title: string }> = (props) => {

  const onCopy = async () => {
    await writeClipboard(props.key);
    toast("Key Copied!");
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
  const klinkId = props.klinkId;
  const store = shareKlinkStore(klinkId);

  const unsub = store.listen(event => {
    switch (event.type) {
      case "success":
        toast.success("Klink shared!");
        break;
      case "failure":
        toast.error("Something went wrong!");
        break;
      case "readWrite":
        break;
      case "readOnly":
        break;
    }
  });

  onCleanup(() => unsub());

  return (
    <div class="flex flex-col space-y-2">
      <p class="text-lg">Share Controls - <b>{store.klinkStore.klink.name}</b></p>
      <p class="font-light text-sm text-zinc-400">Manage sharing information for this collection.</p>
      {/* Keys Row */}
      <Switch>
        {/* Shared Component */}
        <Match when={store.klinkStore.isShared}>
          <div class="flex w-full justify-between space-x-4 py-6">
            <KlinkKeyField key={store.klinkStore.klink.readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={store.klinkStore.klink.writeKey} title={"Write Key"} />
          </div>
          <button class="btn btn-primary btn-sm" onClick={store.createShareLink}>Share</button>
          <button class="btn btn-primary btn-sm" onClick={store.createReadOnlyLink}>Share as Read Only</button>
        </Match>

        {/* Local Only */}
        <Match when={!store.klinkStore.isShared}>
          <div class="pt-4"></div>
          <GlobeLock size={24} />
          <p class="text-lg">This klink is <b>local only</b>.</p>
          <p class="pb-2">To be able to share it with others, <b>upload</b> the collection to the cloud first.</p>
          <button class="btn btn-primary btn-sm" onClick={store.shareKlink}>Upload</button>
        </Match>
      </Switch>
    </div>
  );
}

export default ShareKlinkModal;
