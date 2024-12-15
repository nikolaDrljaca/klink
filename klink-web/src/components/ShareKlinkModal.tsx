import { GlobeLock } from "lucide-solid";
import { Component, Match, onCleanup, Show, Switch } from "solid-js";
import toast from "solid-toast";
import shareKlinkStore from "~/lib/shareKlinkStore";
import KlinkKeyField from "~/components/KlinkKeyField";
import { writeClipboard } from "@solid-primitives/clipboard";

type ShareKlinkModalProps = {
  klinkId: string,
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
        writeClipboard(event.url)
          .then(() => toast("Copied URL to Cliboard."));
        break;
      case "readOnly":
        writeClipboard(event.url)
          .then(() => toast("Copied URL to Cliboard."));
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
          <div class="flex flex-col lg:flex-row w-full space-y-4 lg:space-x-4 lg:space-y-0 py-6">
            <KlinkKeyField key={store.klinkStore.klink.readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={store.klinkStore.klink.writeKey} title={"Write Key"} />
          </div>
          <button class="btn btn-primary btn-sm" onClick={store.createShareLink}>Share</button>
          <button class="btn btn-primary btn-sm btn-outline" onClick={store.createReadOnlyLink}>Share as Read Only</button>
        </Match>

        {/* Local Only */}
        <Match when={!store.klinkStore.isShared}>
          <div class="pt-4"></div>
          <GlobeLock size={24} />
          <p class="text-lg">This klink is <b>local only</b>.</p>
          <p class="pb-2">To be able to share it with others, <b>upload</b> the collection to the cloud first.</p>
          <button class="btn btn-primary btn-sm" onClick={store.shareKlink}>
            <Show when={store.klinkStore.loading}>
              <span class="loading loading-spinner"></span>
            </Show>
            Upload
          </button>
        </Match>
      </Switch>
    </div>
  );
}

export default ShareKlinkModal;
