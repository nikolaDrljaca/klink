import { Copy, GlobeLock } from "lucide-solid";
import { Component, For, Match, onCleanup, Show, Switch } from "solid-js";
import toast from "solid-toast";
import KlinkKeyField from "~/components/KlinkKeyField";
import { writeClipboard } from "@solid-primitives/clipboard";
import shareKlinkStore from "~/pages/components/klink-share-modal/share-klink-store";
import SocialsRow from "./SocialsRow";

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
    }
  });

  const onCopy = () => {
    writeClipboard(store.klinkStore.shareLink)
      .then(() => toast("Copied URL to clipboard."))
  }

  onCleanup(() => unsub());

  return (
    <div class="flex flex-col space-y-2">
      <p class="text-lg">Share Controls - <b>{store.klinkStore.klink.name}</b></p>
      {/* Keys Row */}
      <Switch>
        {/* Shared Component */}
        <Match when={store.klinkStore.isShared}>
          <div class="flex flex-col lg:flex-row w-full space-y-4 lg:space-x-4 lg:space-y-0 py-6">
            <KlinkKeyField key={store.klinkStore.klink.readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={store.klinkStore.klink.writeKey} title={"Write Key"} />
          </div>
          <div class="tooltip" data-tip="Enable to prevent the editing of the Klink by those you share it with.">
            <div class="form-control">
              <label class="label cursor-pointer">
                <span class="">Share as Read Only</span>
                <input type="checkbox" class="toggle" checked={store.klinkStore.readOnlyChecked} onChange={store.setReadOnlyChecked} />
              </label>
            </div>
          </div>

          <div class="divider">Share</div>

          {/* Socials Row */}
          <SocialsRow shareTarget={store.klinkStore.socialShareTarget} />

          {/* URL Copy Field */}
          <div class="join">
            <input
              type="text"
              value={store.klinkStore.shareLink}
              disabled={true}
              class="input input-bordered w-full join-item" />
            <button class="btn join-item" onClick={onCopy}>
              <Copy size={14} />
            </button>
          </div>
        </Match>

        {/* Local Only */}
        <Match when={!store.klinkStore.isShared}>
          <div class="pt-4"></div>
          <div class="flex flex-col space-y-2 items-center text-center pb-4">
            <GlobeLock size={48} />
            <p class="text-lg">This klink is <b>local only</b>.</p>
            <p class="">To be able to share it with others, <b>upload</b> the collection to the cloud first.</p>
          </div>
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
